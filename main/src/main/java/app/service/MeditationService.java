package app.service;

import app.dto.meditation.Meditation;
import app.dto.meditation.MeditationStatus;
import app.dto.meditation.MeditationUploadBodyRequest;
import app.dto.meditation.UploadResponse;
import app.entity.meditation.MeditationEntity;
import app.entity.userattributes.Role;
import app.mapper.MeditationMapper;
import app.repository.MeditationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class MeditationService {
    private final WebClientRestService webClientRestService;
    private final MeditationRepository meditationRepository;
    private final MeditationMapper meditationMapper;

    @Value("${server.meditation.upload-url}")
    private String uploadMeditationUrl;
    @Value("${server.meditation.get-delete-url}")
    private String serviceUrl;
    public UUID uploadMeditation(UserDetails userDetails,
                            MeditationUploadBodyRequest meditationUploadBodyRequest) {
        checkUserRole(userDetails);
        UploadResponse ans = webClientRestService.post(uploadMeditationUrl, meditationUploadBodyRequest);
        return meditationRepository.save(meditationMapper.uploadResponseDataToMeditationEntity(ans.getUploadData())).getId();
    }
    public List<Meditation> getAll() {
        return meditationMapper.meditationEntitiesToMeditations(meditationRepository.findAllByStatus(MeditationStatus.DONE));
    }
    public List<Meditation> getNewMeditations() {
        return meditationMapper.meditationEntitiesToMeditations(meditationRepository.findNewMeditations());
    }
    public void delete(UserDetails userDetails, UUID id) {
        checkUserRole(userDetails);
        Optional<MeditationEntity> meditation = meditationRepository.findById(id);

        if (meditation.isEmpty()) {
            throw new IllegalArgumentException("not found");
        }

        webClientRestService.delete(serviceUrl, Map.of("video-id", id.toString()));
        meditationRepository.delete(meditation.get());
    }
    private void checkUserRole(UserDetails userDetails) {
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN))) {
            throw new AccessDeniedException("access deny");
        }
    }
}
