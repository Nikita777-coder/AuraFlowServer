package app.service;

import app.dto.meditation.MeditationServiceData;
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

    @Value("${server.meditation.get-delete-url}")
    private String videoServiceUrl;

    @Value("${server.web.max-count-requests}")
    private int maxCountRequests;
    @Async
    @Scheduled(fixedRateString = "${server.meditation.fixed-rate-time}")
    public void fetchUploadedMeditationsDataInfo() {
        List<MeditationEntity> uploadingMeditations = meditationRepository
                .findAllByStatus(MeditationStatus.UPLOADING)
                .stream()
                .limit(maxCountRequests)
                .toList();

        List<MeditationEntity> updatedEntities = new ArrayList<>(maxCountRequests);

        for (var video: uploadingMeditations) {
            MeditationServiceData meditationServiceData =
                    webClientRestService.get(videoServiceUrl, Map.of("video-id", video.getVideoId().toString()), MeditationServiceData.class);
            updatedEntities.add(meditationMapper.meditationServiceDataToMeditationEntity(meditationServiceData, video));
        }

        meditationRepository.saveAll(updatedEntities);
    }
}
