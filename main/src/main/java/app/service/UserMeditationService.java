package app.service;

import app.dto.meditation.Status;
import app.dto.meditation.UserMeditation;
import app.dto.meditation.UserMeditationUpdateRequest;
import app.dto.meditation.UserMeditationUploadRequest;
import app.dto.meditationalbum.MeditationAlbumRequest;
import app.entity.UserMeditationAlbumEntity;
import app.entity.usermeditation.UserMeditationEntity;
import app.extra.ProgramCommons;
import app.mapper.StatusMapper;
import app.mapper.UserMeditationMapper;
import app.repository.MeditationAlbumRepository;
import app.repository.MeditationRepository;
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
    private final UserMeditationRepository userMeditationRepository;
    private final UserMeditationMapper userMeditationMapper;
    private final UserService userService;
    private final StatusMapper statusMapper;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UUID addMeditationToUser(UserDetails userDetails, UserMeditationUploadRequest userMeditationUploadRequest) {
        if (userMeditationUploadRequest.getId() == null && userMeditationUploadRequest.getVideoUrl() == null) {
            throw new IllegalArgumentException("you must specify meditationPlatformId or generatedVideoUrl");
        }

        UserMeditationEntity userMeditationEntity = new UserMeditationEntity();
        userMeditationEntity.setUser(userService.getUserByEmail(userDetails.getUsername()));
        List<Status> statuses = new ArrayList<>();
        statuses.add(Status.UNWATCHED);

        if (userMeditationUploadRequest.getId() != null) {
            var meditation = meditationRepository.findById(userMeditationUploadRequest.getId());
            if (meditation.isEmpty()) {
                throw new IllegalArgumentException("no such meditation");
            }

            userMeditationEntity.setMeditationFromPlatform(meditation.get());
        } else if (userMeditationUploadRequest.getVideoUrl() != null) {
            userMeditationEntity.setGeneratedMeditationLink(userMeditationUploadRequest.getVideoUrl());
            statuses.add(Status.GENERATED);
        }

        if (userMeditationUploadRequest.getTitle() != null) {
            userMeditationEntity.setTitle(userMeditationUploadRequest.getTitle());
        }

        userMeditationEntity.setStatuses(statusMapper.listOfStatusesToString(statuses));
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
        medtitationAlbumService.updateAlbumCheckedMeditations(album, albumMeditations);

        return userMeditationEntity.getId();
    }
    public List<UserMeditation> getUserAll(UserDetails userDetails, List<Status> statuses) {
        var entities = userMeditationRepository.findAllByUser_Email(
                userDetails.getUsername()
        );

        List<UserMeditationEntity> newEntities = new ArrayList<>();

        for (var status : statuses) {
            newEntities.addAll(
                    entities.stream().filter(
                                    el -> statusMapper
                                            .stringStatusesToSetOfStatuses(el
                                                    .getStatuses())
                                            .contains(status))
                            .toList()
            );
        }

        if (newEntities.isEmpty()) {
            newEntities = entities;
        }

        return userMeditationMapper.userMeditationEntitiesToUserMeditations(
                newEntities
        );
    }
    public UserMeditation update(UserDetails userDetails, UserMeditationUpdateRequest meditationUpdateRequest) {
        UserMeditationEntity userMeditationEntity = checkAccessAndGet(meditationUpdateRequest.getId(), userDetails);

        userMeditationEntity = userMeditationMapper.updateEntity(meditationUpdateRequest, userMeditationEntity);

        if (meditationUpdateRequest.getPauseTime() < 0.0) {
            throw new IllegalArgumentException("invalid pause time value");
        }

        if (meditationUpdateRequest.getStatuses().stream().filter(el -> el == Status.INPROGRESS).findFirst().isEmpty() &&
                (userMeditationEntity.getPauseTime() > 0.0 || meditationUpdateRequest.getPauseTime() > 0.0)) {
            meditationUpdateRequest.getStatuses().add(Status.INPROGRESS);
        }

        if (meditationUpdateRequest.getStatuses() != null) {
            userMeditationEntity.setStatuses(statusMapper.listOfStatusesToString(meditationUpdateRequest.getStatuses()));
        }

        return userMeditationMapper.userMeditationEntityToUserMeditation(userMeditationRepository.save(userMeditationEntity));
    }
    public void delete(UserDetails userDetails, UUID id) {
        var entity = checkAccessAndGet(id, userDetails);
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
