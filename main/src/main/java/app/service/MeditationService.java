package app.service;

import app.dto.meditation.*;
import app.entity.meditation.MeditationEntity;
import app.extra.ProgramCommons;
import app.mapper.MeditationMapper;
import app.mapper.TagMapper;
import app.repository.MeditationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class MeditationService {
    private final ProgramCommons programCommons;
    private final TagMapper tagMapper;
    private final WebClientRestService webClientRestService;
    private final MeditationRepository meditationRepository;
    private final MeditationMapper meditationMapper;
    private final ProgramCommons storageParamsManager;

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
                                              List<Tag> tags,
                                              boolean isPromoted) {
        programCommons.checkUserRole(userDetails);
        UploadResponseFull ans = webClientRestService.postVideo(
                integrationServiceBaseUrl,
                videoStorageUri + "/by-upload-video",
                title,
                file,
                description,
                UploadResponseFull.class
        );

        var entity = meditationMapper.uploadResponseDataToMeditationEntity(ans);

        if (ans.getUploadResponse().getData().getStatus() == null) {
            entity.setStatus(MeditationStatus.DONE);
        }

        if (author != null) {
            entity.setAuthor(author);
        }

        if (tags != null && !tags.isEmpty()) {
            entity.setTags(tagMapper.tagsToTagsEntities(tags));
        }

        entity.setPromoted(isPromoted);

        return meditationRepository.save(entity).getId();
    }
    public UUID uploadMeditationByUrl(UserDetails userDetails,
                            MeditationUploadBodyRequest meditationUploadBodyRequest) {
        programCommons.checkUserRole(userDetails);
        UploadResponseFull ans = webClientRestService.post(integrationServiceBaseUrl, videoStorageUri + "/by-url", meditationUploadBodyRequest, UploadResponseFull.class);

        var entity = meditationMapper.uploadResponseDataToMeditationEntity(ans);

        if (meditationUploadBodyRequest.getAuthor() != null) {
            entity.setAuthor(meditationUploadBodyRequest.getAuthor());
        }

        if (meditationUploadBodyRequest.getTags() != null && !meditationUploadBodyRequest.getTags().isEmpty()) {
            entity.setTags(tagMapper.tagsToTagsEntities(meditationUploadBodyRequest.getTags()));
        }

        return meditationRepository.save(entity).getId();
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Meditation> getAll() {
        return meditationMapper.meditationEntitiesToMeditations(meditationRepository.findAllByStatusIn(List.of(MeditationStatus.DONE)));
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
                        MeditationStatus.DONE)
        );
    }
    public void delete(UserDetails userDetails, UUID id) {
        programCommons.checkUserRole(userDetails);
        MeditationEntity meditation = getMeditation(id);

        webClientRestService.delete(integrationServiceBaseUrl, videoStorageUri, storageParamsManager.getParams().
                get(type.toLowerCase()).getParams(meditation)
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
            meditation.setTags(tagMapper.tagsToTagsEntities(request.getTags()));
        }

        return meditationMapper.meditationEntityToMeditation(
                meditationRepository.save(
                       meditation
                )
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
