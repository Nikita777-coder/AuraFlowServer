package app.service;

import app.dto.meditation.MeditationServiceDataWrapper;
import app.dto.meditation.MeditationStatus;
import app.entity.meditation.MeditationEntity;
import app.mapper.MeditationMapper;
import app.repository.MeditationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@EnableAsync
@RequiredArgsConstructor
public class AppRegularActions {
    private final MeditationRepository meditationRepository;
    private final WebClientRestService webClientRestService;
    private final MeditationMapper meditationMapper;

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
                                mainUri,
                                Map.of("video-id",
                                        video.getVideoId().toString()
                                ),
                                MeditationServiceDataWrapper.class);
                meditationServiceData.getData().setStatus(meditationServiceData.getData().getStatus().toUpperCase());
                updatedEntities.add(meditationMapper.meditationServiceDataToMeditationEntity(
                        meditationServiceData.getData(),
                        video)
                );

                if (videoStorageType.equalsIgnoreCase("yandex")) {
                    webClientRestService.post(
                            integrationServiceBaseUrl,
                            uploadPath,
                            meditationServiceData,
                            String.class
                    );
                }
            } catch (IllegalStateException ex) {
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
                } catch (IllegalStateException ex) {
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
                if (!webClientRestService.get(
                        integrationServiceBaseUrl,
                        mainUri,
                        Map.of("video-link",
                                video.getVideoLink()
                        ),
                        String.class)
                        .equals("success")) {
                    deleteEntities.add(video);
                }
            }

            deleteUselessMeditations(deleteEntities);
        }
    }

    @Async
    public void deleteUselessMeditations(List<MeditationEntity> entities) {
        meditationRepository.deleteAllByIdInBatch(entities.stream().map(MeditationEntity::getId).toList());
    }
}
