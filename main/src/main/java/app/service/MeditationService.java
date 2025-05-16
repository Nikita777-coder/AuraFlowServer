package app.service;

import app.dto.meditation.*;
import app.entity.MeditationPlatformAlbumEntity;
import app.entity.MeditationEntity;
import app.extra.ProgramCommons;
import app.mapper.MeditationMapper;
import app.mapper.TagMapper;
import app.repository.MeditationPlatformAlbumRepository;
import app.repository.MeditationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class MeditationService {
    private final ProgramCommons programCommons;
    private final TagMapper tagMapper;
    private final WebClientRestService webClientRestService;
    private final MeditationRepository meditationRepository;
    private final MeditationMapper meditationMapper;

    @Value("${server.integration.video-storage.uri}")
    private String videoStorageUri;

    @Value("${server.integration.base-url}")
    private String integrationServiceBaseUrl;

    @Value("${server.integration.video-storage.type}")
    private String type;

    public UUID uploadMeditationByUploadVideo(UserDetails userDetails,
                                              MultipartFile file,
                                              String title,
                                              String description,
                                              String author,
                                              List<String> tags,
                                              boolean isPromoted) {
        programCommons.checkUserRole(userDetails);
        UUID answer = webClientRestService.postVideo(
                integrationServiceBaseUrl,
                videoStorageUri + "/by-upload-video",
                title,
                file,
                description,
                UUID.class
        );
        UploadResponseFull ans = webClientRestService.get(
                integrationServiceBaseUrl,
                videoStorageUri + "/get-data-info",
                Map.of("task-id", answer.toString()),
                UploadResponseFull.class
        );

        var entity = meditationMapper.uploadResponseDataToMeditationEntity(ans);
        entity.setTaskId(answer);
        entity.setCreatedAt(LocalDateTime.now());

        entity.setAuthor("service");
        if (entity.getAuthor() == null) {
            entity.setAuthor(author);
        }

        if (tags != null && !tags.isEmpty()) {
            entity.setJsonTags(tagMapper.tagsToJsonStringTags(tags));
        }

        entity.setPromoted(isPromoted);

        return meditationRepository.save(entity).getId();
    }
    public List<String> getAllPlatformMeditations() {

        ParameterizedTypeReference<List<String>> p = new ParameterizedTypeReference<List<String>>() {};

        return webClientRestService.get(
                integrationServiceBaseUrl,
                videoStorageUri + "/all",
                p
        );
    }
    public UploadStatus getMeditationUploadStatus(UserDetails userDetails, UUID meditationId) {
        programCommons.checkUserRole(userDetails);
        var meditation = meditationRepository.findById(meditationId).orElseThrow(()
                -> new IllegalArgumentException("meditation not found"));

        UploadResponseFull ans = webClientRestService.get(
                integrationServiceBaseUrl,
                videoStorageUri + "/get-data-info",
                Map.of("task-id", meditation.getTaskId().toString()
                ),
                UploadResponseFull.class);

        var entity = meditationMapper.meditationServiceDataToMeditationEntity(
                ans,
                meditation
        );
        entity.setCountStatusRequests(meditation.getCountStatusRequests() + 1);
        entity.setUpdateAt(LocalDateTime.now());

        meditationRepository.save(entity);

        return entity.getStatus();
    }
    public UUID uploadMeditationByUrl(UserDetails userDetails,
                                      MeditationUploadBodyRequest meditationUploadBodyRequest) {
        programCommons.checkUserRole(userDetails);
        UUID answer = webClientRestService.post(integrationServiceBaseUrl, videoStorageUri + "/by-url", meditationUploadBodyRequest, UUID.class);
        UploadResponseFull ans = webClientRestService.get(
                integrationServiceBaseUrl,
                videoStorageUri + "/get-data-info",
                Map.of("task-id", answer.toString()),
                UploadResponseFull.class
        );

        var entity = meditationMapper.uploadResponseDataToMeditationEntity(ans);
        entity.setTaskId(answer);
        entity.setTitle(meditationUploadBodyRequest.getTitle());
        entity.setCreatedAt(LocalDateTime.now());

        if (meditationUploadBodyRequest.getAuthor() != null) {
            entity.setAuthor(meditationUploadBodyRequest.getAuthor());
        }

        if (meditationUploadBodyRequest.getDescription() != null) {
            entity.setDescription(meditationUploadBodyRequest.getDescription());
        }

        if (meditationUploadBodyRequest.getTags() != null && !meditationUploadBodyRequest.getTags().isEmpty()) {
            entity.setJsonTags(tagMapper.tagsToJsonStringTags(meditationUploadBodyRequest.getTags()));
        }

        return meditationRepository.save(entity).getId();
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Meditation> getAll() {
        return meditationMapper.meditationEntitiesToMeditations(meditationRepository.findAllByStatusIn(List.of(UploadStatus.READY)));
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Meditation> getRecommended() {
        return meditationMapper.meditationEntitiesToMeditations(meditationRepository.findAllByPromoted(true));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Meditation> getNewMeditations() {
        return meditationMapper.meditationEntitiesToMeditations(meditationRepository
                .findAllByCreatedAtAfterAndStatus(
                        LocalDateTime.now().minusDays(14),
                        UploadStatus.READY)
        );
    }
    public void delete(UserDetails userDetails, UUID id) {
        programCommons.checkUserRole(userDetails);
        MeditationEntity meditation = getMeditation(id);

        webClientRestService.delete(
                integrationServiceBaseUrl,
                videoStorageUri,
                Map.of("video-link", meditation.getVideoLink())
        );

        meditationRepository.delete(meditation);
    }
    public Meditation update(UserDetails userDetails, MeditationUpdateRequest request) {
        programCommons.checkUserRole(userDetails);
        MeditationEntity meditation = getMeditation(request.getId());

        meditation = meditationMapper.updateMeditationEntity(
                meditation, request
        );

        if (request.getTags() != null) {
            meditation.setJsonTags(tagMapper.tagsToJsonStringTags(request.getTags()));
        }

        var en = meditationRepository.save(
                meditation
        );

        return meditationMapper.meditationEntityToMeditation(
                en
        );
    }
    private MeditationEntity getMeditation(UUID id) {
        Optional<MeditationEntity> meditation = meditationRepository.findById(id);

        if (meditation.isEmpty()) {
            throw new IllegalArgumentException("not found");
        }

        return meditation.get();
    }
}
