package app.service;

import app.dto.meditation.Meditation;
import app.dto.meditation.MeditationStatus;
import app.dto.meditation.MeditationUploadBodyRequest;
import app.dto.meditation.UploadResponseFull;
import app.entity.meditation.MeditationEntity;
import app.entity.userattributes.Role;
import app.extra.storageparams.StorageParamsManager;
import app.mapper.MeditationMapper;
import app.repository.MeditationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final WebClientRestService webClientRestService;
    private final MeditationRepository meditationRepository;
    private final MeditationMapper meditationMapper;
    private final StorageParamsManager storageParamsManager;

    @Value("${server.integration.video-storage.uri}")
    private String videoStorageUri;

    @Value("${server.integration.base-url}")
    private String integrationServiceBaseUrl;

    @Value("${server.integration.video-storage.type}")
    private String type;

    public UUID uploadMeditationByUploadVideo(UserDetails userDetails,
                                              MultipartFile file,
                                              String title,
                                              String description) {
        checkUserRole(userDetails);
        UploadResponseFull ans = webClientRestService.postVideo(
                integrationServiceBaseUrl,
                videoStorageUri + "/by-upload-video",
                title,
                file,
                description,
                UploadResponseFull.class
        );

        var entity = meditationMapper.uploadResponseDataToMeditationEntity(ans.getUploadResponse().getData());
        entity.setWasUploadedFromUrl(ans.isWasUploadFromUrl());

        return meditationRepository.save(entity).getId();
    }

    public UUID uploadMeditationByUrl(UserDetails userDetails,
                            MeditationUploadBodyRequest meditationUploadBodyRequest) {
        checkUserRole(userDetails);
        UploadResponseFull ans = webClientRestService.post(integrationServiceBaseUrl, videoStorageUri + "/by-url", meditationUploadBodyRequest, UploadResponseFull.class);
        return meditationRepository.save(meditationMapper.uploadResponseDataToMeditationEntity(ans.getUploadResponse().getData())).getId();
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Meditation> getAll() {
        return meditationMapper.meditationEntitiesToMeditations(meditationRepository.findAllByStatus(MeditationStatus.DONE));
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
        checkUserRole(userDetails);
        Optional<MeditationEntity> meditation = meditationRepository.findById(id);

        if (meditation.isEmpty()) {
            throw new IllegalArgumentException("not found");
        }

        webClientRestService.delete(integrationServiceBaseUrl, videoStorageUri, storageParamsManager.getParams().
                get(type.toLowerCase()).getParams(meditation.get())
        );

        meditationRepository.delete(meditation.get());
    }
    private void checkUserRole(UserDetails userDetails) {
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN))) {
            throw new AccessDeniedException("access deny");
        }
    }
}
