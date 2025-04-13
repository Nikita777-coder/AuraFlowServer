package app.service;

import app.dto.meditation.*;
import app.dto.meditationalbum.MeditationAlbumRequest;
import app.entity.MeditationAlbumEntity;
import app.entity.usermeditation.StatusEntity;
import app.entity.usermeditation.UserMeditationEntity;
import app.extra.ProgramCommons;
import app.mapper.StatusMapper;
import app.mapper.UserMeditationMapper;
import app.repository.MeditationAlbumRepository;
import app.repository.MeditationRepository;
import app.repository.StatusRepository;
import app.repository.UserMeditationRepository;
import lombok.RequiredArgsConstructor;
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
    private final StatusRepository statusRepository;

    public GeneratedMeditation generatedMeditation(String text) {
        List<String> topics = extractMeditationThemesFromText(text);
        throw new RuntimeException();
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UUID addMeditationToUser(UserDetails userDetails, UUID meditationId) {
        var meditation = meditationRepository.findById(meditationId);
        if (meditation.isEmpty()) {
            throw new IllegalArgumentException("no such meditation");
        }

        UserMeditationEntity userMeditationEntity = new UserMeditationEntity();
        userMeditationEntity.setUser(userService.getUserByEmail(userDetails.getUsername()));
        userMeditationEntity.setMeditationFromPlatform(meditation.get());

        StatusEntity statusEntity = new StatusEntity();
        statusEntity.setStatus(Status.UNWATCHED);
        List<StatusEntity> statusEntities = new ArrayList<>();
        statusEntities.add(statusEntity);

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
    private List<String> extractMeditationThemesFromText(String text) {
        throw new RuntimeException();
    }
}
