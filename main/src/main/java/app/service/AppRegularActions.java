package app.service;

import app.dto.meditation.UploadResponseFull;
import app.dto.meditation.UploadStatus;
import app.dto.notificationservice.NotificationRequest;
import app.entity.MeditationEntity;
import app.mapper.MeditationMapper;
import app.repository.MeditationRepository;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppRegularActions {
    private final MeditationRepository meditationRepository;
    private final WebClientRestService webClientRestService;
    private final MeditationMapper meditationMapper;
    private final MeditationService meditationService;
    private final UserRepository userRepository;

    @Value("${server.integration.video-storage.uri}")
    private String mainUri;

    @Value("${server.integration.base-url}")
    private String integrationServiceBaseUrl;

    @Value("${server.web.max-count-requests}")
    private int maxCountRequests;

    @Value("${server.integration.notification-service.message}")
    private String notificationMessage;

    @Value("${server.integration.notification-service.uri}")
    private String notificationUri;

    @Scheduled(fixedRateString = "${server.integration.fixed-rate-time}")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Scheduled(fixedRateString = "${server.integration.fixed-rate-time}")
    public void fetchUploadedMeditationsDataInfo() {
        Mono.fromCallable(() -> meditationRepository.findAllByStatusIn(List.of(
                        UploadStatus.PARSED,
                        UploadStatus.PARSING,
                        UploadStatus.LOADING_TO_STORAGE,
                        UploadStatus.SYSTEM_FILE_COPYING
                )))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(list -> Flux.fromIterable(
                        list.stream().limit(maxCountRequests).toList()
                ))
                .flatMap(video ->
                        webClientRestService.get(
                                        integrationServiceBaseUrl,
                                        mainUri + "/get-data-info",
                                        Map.of("task-id", video.getTaskId().toString()),
                                        UploadResponseFull.class
                                )
                                .map(ans -> {
                                    MeditationEntity entity = meditationMapper.meditationServiceDataToMeditationEntity(ans, video);
                                    entity.setCountStatusRequests(video.getCountStatusRequests() + 1);
                                    entity.setUpdateAt(LocalDateTime.now());
                                    return entity;
                                })
                                .onErrorResume(ex -> {
                                    if ((ex instanceof IllegalStateException || ex instanceof IllegalArgumentException)
                                            && "already got it or not found".equals(ex.getMessage())
                                            && video.getStatus() != UploadStatus.ERROR
                                            && video.getCountStatusRequests() > 0) {

                                        video.setStatus(UploadStatus.READY);
                                        video.setCountStatusRequests(video.getCountStatusRequests() + 1);
                                        video.setUpdateAt(LocalDateTime.now());
                                        return Mono.just(video);
                                    }
                                    return Mono.empty();
                                })
                )
                .collectList()
                .flatMap(updatedList ->
                        Mono.fromRunnable(() -> meditationRepository.saveAll(updatedList))
                                .subscribeOn(Schedulers.boundedElastic())
                )
                .subscribe();
    }

    @Scheduled(fixedRateString = "${server.integration.fixed-rate-time}")
    public void deleteUselessLocalMeditationsFromYandex() {
        Mono.fromCallable(() ->
                        meditationRepository.findAll().stream().limit(maxCountRequests).toList()
                )
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .filter(video -> video.getStatus() == UploadStatus.READY || video.getStatus() == UploadStatus.ERROR)
                .flatMap(video ->
                        webClientRestService.get(
                                        integrationServiceBaseUrl,
                                        mainUri,
                                        Map.of("video-link", video.getVideoLink()),
                                        String.class
                                )
                                .then(Mono.<MeditationEntity>empty())
                                .onErrorResume(ex -> Mono.just(video))
                )
                .collectList()
                .flatMap(deleteEntities ->
                        Mono.fromRunnable(() -> {
                            List<UUID> idsToDelete = deleteEntities.stream()
                                    .map(MeditationEntity::getId)
                                    .collect(Collectors.toList());

                            meditationRepository.deleteAllByIdInBatch(idsToDelete);
                        }).subscribeOn(Schedulers.boundedElastic())
                )
                .subscribe();
    }

    @Scheduled(fixedRateString = "${server.integration.fixed-update}")
    public void notificateUsers() {
        Mono.fromCallable(() -> userRepository.getAllByHasPractiseBreathOpt(true))
                .subscribeOn(Schedulers.boundedElastic())
                .map(users -> users.stream()
                        .filter(user -> user.getStartTimeOfBreathPractise().isBefore(LocalTime.now())
                                && user.getStopTimeOfBreathPractise().isAfter(LocalTime.now()))
                        .toList())
                .flatMap(filteredUsers -> {
                    if (filteredUsers.isEmpty()) return Mono.empty();

                    NotificationRequest req = new NotificationRequest();
                    req.setListTo(filteredUsers.stream().map(u -> u.getOneSignalId().toString()).toList());
                    req.setMessage(notificationMessage);

                    return webClientRestService.post(
                            integrationServiceBaseUrl,
                            notificationUri,
                            req,
                            Void.class
                    );
                })
                .subscribe();
    }

    @Async
    @Scheduled(fixedRateString = "${server.integration.fixed-update}")
    public void loadMeditations() {
        List<String> allPlatformMeditations = meditationService.getAllPlatformMeditations();
        List<MeditationEntity> allLocalMeditations = meditationRepository.findAll();
        Set<String> meditationLocalVideos = allLocalMeditations.stream().map(MeditationEntity::getVideoLink).collect(Collectors.toSet());
        List<MeditationEntity> videoLinksForAddingToLocalMeditations = new ArrayList<>();

        for (var meditation: allPlatformMeditations) {
            if (!meditationLocalVideos.contains(meditation)) {
                MeditationEntity meditationEntity = new MeditationEntity();
                meditationEntity.setCreatedAt(LocalDateTime.now());
                meditationEntity.setTitle(meditation.split("/")[5].split("\\.")[0]);
                meditationEntity.setVideoLink(meditation);
                meditationEntity.setStatus(UploadStatus.READY);

                videoLinksForAddingToLocalMeditations.add(meditationEntity);
            }
        }

        meditationRepository.saveAll(videoLinksForAddingToLocalMeditations);
    }
}
