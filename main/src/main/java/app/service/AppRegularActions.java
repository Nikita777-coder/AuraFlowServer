package app.service;

import app.dto.meditation.MeditationServiceDataWrapper;
import app.dto.meditation.UploadResponseFull;
import app.dto.meditation.UploadStatus;
import app.dto.notificationservice.NotificationRequest;
import app.entity.UserEntity;
import app.entity.MeditationEntity;
import app.extra.ProgramCommons;
import app.mapper.MeditationMapper;
import app.repository.MeditationRepository;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@EnableAsync
@RequiredArgsConstructor
public class AppRegularActions {
    private final MeditationRepository meditationRepository;
    private final WebClientRestService webClientRestService;
    private final MeditationMapper meditationMapper;
    private final UserRepository userRepository;
    private final MeditationService meditationService;

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
//    @Async
//    @Scheduled(fixedRateString = "${server.integration.fixed-rate-time}")
//    @Transactional(isolation = Isolation.REPEATABLE_READ)
//    public void fetchUploadedMeditationsDataInfo() {
//        List<MeditationEntity> uploadingMeditations = meditationRepository
//                .findAllByStatusIn(List.of(UploadStatus.PARSED, UploadStatus.PARSING, UploadStatus.LOADING_TO_STORAGE, UploadStatus.SYSTEM_FILE_COPYING))
//                .stream()
//                .limit(maxCountRequests)
//                .toList();
//
//        List<MeditationEntity> updatedEntities = new ArrayList<>(maxCountRequests);
//        List<MeditationEntity> deleteEntities = new ArrayList<>(maxCountRequests);
//
//        for (var video: uploadingMeditations) {
//            try {
//                UploadResponseFull ans = webClientRestService.get(
//                        integrationServiceBaseUrl,
//                        mainUri + "/get-data-info",
//                        Map.of("task-id", video.getTaskId().toString()),
//                        UploadResponseFull.class
//                );
//
//                var entity = meditationMapper.meditationServiceDataToMeditationEntity(
//                        ans,
//                        video
//                );
//                entity.setCountStatusRequests(video.getCountStatusRequests() + 1);
//                entity.setUpdateAt(LocalDateTime.now());
//
//                updatedEntities.add(entity);
//            } catch (IllegalStateException | IllegalArgumentException ex) {
//                if (ex.getMessage().equals("already got it or not found") && video.getStatus() != UploadStatus.ERROR && video.getCountStatusRequests() > 0) {
//                    video.setStatus(UploadStatus.READY);
//                    video.setCountStatusRequests(video.getCountStatusRequests() + 1);
//                    video.setUpdateAt(LocalDateTime.now());
//
//                    updatedEntities.add(video);
//                    continue;
//                }
//
//                deleteEntities.add(video);
//            }
//        }
//
//        deleteUselessMeditations(deleteEntities);
//        meditationRepository.saveAll(updatedEntities);
//    }
//
//    @Async
//    @Scheduled(fixedRateString = "${server.integration.fixed-rate-time}")
//    public void deleteUselessLocalMeditationsFromYandex() {
//        List<MeditationEntity> uploadingMeditations = meditationRepository.findAll().stream().limit(maxCountRequests).toList();
//        List<MeditationEntity> deleteEntities = new ArrayList<>(maxCountRequests);
//
//        for (var video : uploadingMeditations) {
//            if (video.getStatus() == UploadStatus.READY || video.getStatus() == UploadStatus.ERROR) {
//                try {
//                    webClientRestService.get(
//                            integrationServiceBaseUrl,
//                            mainUri,
//                            Map.of("video-link", video.getVideoLink()),
//                            String.class);
//                } catch (IllegalArgumentException | IllegalStateException ex) {
//                    deleteEntities.add(video);
//                }
//            }
//        }
//
//        deleteUselessMeditations(deleteEntities);
//    }
//
//    @Async
//    public void deleteUselessMeditations(List<MeditationEntity> entities) {
//        meditationRepository.deleteAllByIdInBatch(entities.stream().map(MeditationEntity::getId).toList());
//    }
//
//    @Async
//    @Scheduled(fixedRateString = "${server.integration.fixed-update}")
//    public void notificateUsers() {
//        List<UserEntity> userEntities = userRepository.getAllByHasPractiseBreathOpt(true);
//        userEntities = userEntities.stream().filter(user ->
//                user.getStartTimeOfBreathPractise().isBefore(LocalTime.now())
//                        && user.getStopTimeOfBreathPractise().isAfter(LocalTime.now())).toList();
//
//        if (!userEntities.isEmpty()) {
//            NotificationRequest notificationRequest = new NotificationRequest();
//            notificationRequest.setListTo(userEntities.stream().map(user -> user.getOneSignalId().toString()).toList());
//            notificationRequest.setMessage(notificationMessage);
//
//            webClientRestService.post(
//                    integrationServiceBaseUrl,
//                    notificationUri,
//                    notificationRequest,
//                    void.class
//            );
//        }
//    }

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
