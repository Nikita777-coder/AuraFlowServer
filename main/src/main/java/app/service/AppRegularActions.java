package app.service;

import app.dto.meditation.MeditationServiceDataWrapper;
import app.dto.meditation.MeditationStatus;
import app.dto.notificationservice.NotificationRequest;
import app.entity.UserEntity;
import app.entity.meditation.MeditationEntity;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@EnableAsync
@RequiredArgsConstructor
public class AppRegularActions {
    private final MeditationRepository meditationRepository;
    private final WebClientRestService webClientRestService;
    private final MeditationMapper meditationMapper;
    private final ProgramCommons storageParamsManager;
    private final UserRepository userRepository;

    @Value("${server.integration.video-storage.type}")
    private String videoStorageType;

    @Value("${server.integration.video-storage.uri}")
    private String mainUri;

    @Value("${server.integration.base-url}")
    private String integrationServiceBaseUrl;

    @Value("${server.integration.video-storage.upload-path}")
    private String uploadPath;

    @Value("${server.web.max-count-requests}")
    private int maxCountRequests;

    @Value("${server.integration.video-storage.kinescope-uri}")
    private String kinescopeUri;

    @Value("${server.integration.notification-service.message}")
    private String notificationMessage;

    @Value("${server.integration.notification-service.uri}")
    private String notificationUri;
    @Async
    @Scheduled(fixedRateString = "${server.integration.fixed-rate-time}")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void fetchUploadedMeditationsDataInfo() {
        List<MeditationEntity> uploadingMeditations = meditationRepository
                .findAllByStatusIn(List.of(MeditationStatus.UPLOADING, MeditationStatus.PROCESSING))
                .stream()
                .limit(maxCountRequests)
                .toList();

        List<MeditationEntity> updatedEntities = new ArrayList<>(maxCountRequests);
        List<MeditationEntity> deleteEntities = new ArrayList<>(maxCountRequests);

        for (var video: uploadingMeditations) {
            try {
                MeditationServiceDataWrapper meditationServiceData =
                        webClientRestService.get(
                                integrationServiceBaseUrl,
                                kinescopeUri,
                                Map.of("video-id",
                                        video.getVideoId().toString()
                                ),
                                MeditationServiceDataWrapper.class);
                meditationServiceData.getData().setStatus(meditationServiceData.getData().getStatus().toUpperCase());

                if (videoStorageType.equalsIgnoreCase("yandexcloud") &&
                        meditationServiceData.getData().getStatus().equals("DONE")) {
                    webClientRestService.post(
                            integrationServiceBaseUrl,
                            uploadPath,
                            meditationMapper.meditationEntityToUploadResponseFull(video),
                            String.class
                    );


                }

                updatedEntities.add(meditationMapper.meditationServiceDataToMeditationEntity(
                        meditationServiceData.getData(),
                        video)
                );
            } catch (IllegalStateException | IllegalArgumentException ex) {
                deleteEntities.add(video);
            }
        }

        deleteUselessMeditations(deleteEntities);
        meditationRepository.saveAll(updatedEntities);
    }

    @Async
    @Scheduled(fixedRateString = "${server.integration.fixed-rate-time}")
    public void deleteUselessLocalMeditations() {
        if (videoStorageType.equals("kinescope")) {
            List<MeditationEntity> uploadingMeditations = meditationRepository.findAll().stream().limit(maxCountRequests).toList();
            List<MeditationEntity> deleteEntities = new ArrayList<>(maxCountRequests);

            for (var video : uploadingMeditations) {
                try {
                    webClientRestService.get(
                            integrationServiceBaseUrl,
                            mainUri,
                            Map.of("video-id",
                                    video.getVideoId().toString()
                            ),
                            MeditationServiceDataWrapper.class);
                } catch (IllegalStateException | IllegalArgumentException ex) {
                    deleteEntities.add(video);
                }
            }

            deleteUselessMeditations(deleteEntities);
        }
    }

    @Async
    @Scheduled(fixedRateString = "${server.integration.fixed-rate-time}")
    public void deleteUselessLocalMeditationsFromYandex() {
        if (videoStorageType.equals("yandexcloud")) {
            List<MeditationEntity> uploadingMeditations = meditationRepository.findAll().stream().limit(maxCountRequests).toList();
            List<MeditationEntity> deleteEntities = new ArrayList<>(maxCountRequests);

            for (var video : uploadingMeditations) {
                if (video.getStatus() != MeditationStatus.UPLOADING && video.getStatus() != MeditationStatus.PROCESSING) {
                    try {
                        webClientRestService.get(
                                integrationServiceBaseUrl,
                                mainUri,
                                storageParamsManager.getParams().get(videoStorageType).getParams(video),
                                String.class);
                    } catch (IllegalArgumentException | IllegalStateException ex) {
                        deleteEntities.add(video);
                    }
                }
            }

            deleteUselessMeditations(deleteEntities);
        }
    }

    @Async
    public void deleteUselessMeditations(List<MeditationEntity> entities) {
        meditationRepository.deleteAllByIdInBatch(entities.stream().map(MeditationEntity::getId).toList());
    }

    @Async
    @Scheduled(fixedRateString = "${server.integration.fixed-update}")
    public void notificateUsers() {
        List<UserEntity> userEntities = userRepository.getAllByHasPractiseBreathOpt(true);
        userEntities = userEntities.stream().filter(user ->
                user.getStartTimeOfBreathPractise().isBefore(LocalTime.now())
                        && user.getStopTimeOfBreathPractise().isAfter(LocalTime.now())).toList();

        if (!userEntities.isEmpty()) {
            NotificationRequest notificationRequest = new NotificationRequest(
                    userEntities.stream().map(user -> user.getOneSignalId().toString()).toList(),
                    notificationMessage
            );

            webClientRestService.post(
                    integrationServiceBaseUrl,
                    notificationUri,
                    notificationRequest,
                    void.class
            );
        }
    }
}
