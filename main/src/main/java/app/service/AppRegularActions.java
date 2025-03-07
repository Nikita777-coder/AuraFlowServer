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
import java.util.List;
import java.util.Map;

@Service
@EnableAsync
@RequiredArgsConstructor
public class AppRegularActions {
    private final MeditationRepository meditationRepository;
    private final WebClientRestService webClientRestService;
    private final MeditationMapper meditationMapper;

    @Value("${server.integration.main-uri}")
    private String mainUri;

    @Value("${server.integration.base-url}")
    private String integrationServiceBaseUrl;

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
        List<MeditationEntity> uploadingMeditations = meditationRepository.findAll().stream().limit(maxCountRequests).toList();
        List<MeditationEntity> deleteEntities = new ArrayList<>(maxCountRequests);

        for (var video: uploadingMeditations) {
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

    @Async
    public void deleteUselessMeditations(List<MeditationEntity> entities) {
        meditationRepository.deleteAllByIdInBatch(entities.stream().map(MeditationEntity::getId).toList());
    }
}
