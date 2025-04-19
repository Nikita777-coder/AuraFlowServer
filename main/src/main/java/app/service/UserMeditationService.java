package app.service;

import app.dto.meditation.*;
import app.dto.meditationalbum.MeditationAlbumRequest;
import app.entity.MeditationAlbumEntity;
import app.entity.usermeditation.StatusEntity;
import app.entity.usermeditation.UserMeditationEntity;
import app.extra.ProgramCommons;
import app.mapper.UserMeditationMapper;
import app.repository.*;
import io.netty.handler.timeout.ReadTimeoutException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class UserMeditationService {
    private final ProgramCommons programCommons;
    private final MeditationRepository meditationRepository;
    private final MedtitationAlbumService medtitationAlbumService;
    private final MeditationAlbumRepository meditationAlbumRepository;
    private final UserMeditationRepository userMeditationRepository;
    private final UserMeditationMapper userMeditationMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final WebClientRestService webClientRestService;

    @Value("${server.integration.base-url}")
    private String integrationBaseUrl;
    @Value("${server.integration.meditation-ai-path}")
    private String integrationGeneratePath;
    public GeneratedMeditation generatedMeditation(UserDetails userDetails,
                                                   ModelMeditationRequest modelMeditationRequest) {
        var user = userService.getUserByEmail(userDetails.getUsername());
        user.setCountOfGenerations(user.getCountOfGenerations() + 1);
        userRepository.save(user);
        GeneratedMeditation generatedMeditation;

        try {
            generatedMeditation = webClientRestService.post(
                    integrationBaseUrl,
                    integrationGeneratePath,
                    modelMeditationRequest,
                    GeneratedMeditation.class
            );
        } catch (ReadTimeoutException ex) {
            user.setCountOfGenerations(user.getCountOfGenerations() - 1);
            userRepository.save(user);
            throw ex;
        }

        return generatedMeditation;
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UUID addMeditationToUser(UserDetails userDetails, UserMeditationUploadRequest userMeditationUploadRequest) {
        if (userMeditationUploadRequest.getId() == null && userMeditationUploadRequest.getVideoUrl() == null) {
            throw new IllegalArgumentException("you must specify meditationPlatformId or generatedVideoUrl");
        }

        UserMeditationEntity userMeditationEntity = new UserMeditationEntity();
        userMeditationEntity.setUser(userService.getUserByEmail(userDetails.getUsername()));
        List<StatusEntity> statusEntities = new ArrayList<>();

        var status = new StatusEntity();
        status.setStatus(Status.UNWATCHED);
        statusEntities.add(status);

        if (userMeditationUploadRequest.getId() != null) {
            var meditation = meditationRepository.findById(userMeditationUploadRequest.getId());
            if (meditation.isEmpty()) {
                throw new IllegalArgumentException("no such meditation");
            }

            userMeditationEntity.setMeditationFromPlatform(meditation.get());
        } else if (userMeditationUploadRequest.getVideoUrl() != null) {
            userMeditationEntity.setGeneratedMeditationLink(userMeditationUploadRequest.getVideoUrl());
            var status2 = new StatusEntity();
            status2.setStatus(Status.GENERATED);

            statusEntities.add(status2);
        }

        userMeditationEntity.setStatuses(statusEntities);
        userMeditationEntity = userMeditationRepository.save(userMeditationEntity);

        var album = medtitationAlbumService.getAlbum("Мои медитации").orElseGet(() -> {
                    MeditationAlbumRequest meditationAlbumRequest = new MeditationAlbumRequest();
                    meditationAlbumRequest.setTitle("Мои медитации");
                    meditationAlbumRequest.setDescription("коллекция всех медитаций, добавленных вами из базы медитаций " +
                            "сервиса или сгенерированных вами с помощью модели");
                    meditationAlbumRequest.setMeditations(List.of());

                    return medtitationAlbumService.createNewAlbum(userDetails, meditationAlbumRequest);
                }
        );

        List<UserMeditationEntity> albumMeditations = album.getMeditations();
        albumMeditations.add(userMeditationEntity);
        medtitationAlbumService.updateAlbumCheckedMeditations(userDetails, album.getId(), albumMeditations);

        return userMeditationEntity.getId();
    }
    public List<UserMeditation> getUserAll(UserDetails userDetails, List<Status> meditationRequest) {
        var entities = userMeditationRepository.findAllByUser_Email(
                userDetails.getUsername()
        );

        return userMeditationMapper.userMeditationEntitiesToUserMeditations(
                entities
        );
    }
    public UserMeditation update(UserDetails userDetails, UserMeditationUpdateRequest meditationUpdateRequest) {
        UserMeditationEntity userMeditationEntity = checkAccessAndGet(meditationUpdateRequest.getId(), userDetails);

        userMeditationEntity = userMeditationMapper.updateEntity(meditationUpdateRequest, userMeditationEntity);
        if (meditationUpdateRequest.getStatuses() != null) {
            userMeditationEntity.setStatuses(statusRepository.findAllByStatusIn(meditationUpdateRequest.getStatuses()));
        }

        return userMeditationMapper.userMeditationEntityToUserMeditation(userMeditationRepository.save(userMeditationEntity));
    }
    public void delete(UserDetails userDetails, UUID id) {
        var entity = checkAccessAndGet(id, userDetails);

        List<MeditationAlbumEntity> albums = entity.getAlbumEntities();

        for (MeditationAlbumEntity album : albums) {
            album.getMeditations().remove(entity);
        }

        meditationAlbumRepository.saveAll(albums);
        userMeditationRepository.delete(entity);
    }
    private UserMeditationEntity checkAccessAndGet(UUID id, UserDetails userDetails) {
        UserMeditationEntity userMeditationEntity = getMeditation(id);

        if (!programCommons.isUserAdmin(userDetails) && !userMeditationEntity.getUser().getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("access deny");
        }

        return userMeditationEntity;
    }
    private UserMeditationEntity getMeditation(UUID id) {
        return userMeditationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("meditation not found"));
    }
}
