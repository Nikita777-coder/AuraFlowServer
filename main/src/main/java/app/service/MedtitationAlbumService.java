package app.service;

import app.dto.meditationalbum.MeditationAlbum;
import app.dto.meditationalbum.MeditationAlbumRequest;
import app.entity.UserMeditationAlbumEntity;
import app.entity.UserEntity;
import app.entity.usermeditation.UserMeditationEntity;
import app.extra.ProgramCommons;
import app.mapper.MeditationAlbumMapper;
import app.repository.MeditationAlbumRepository;
import app.repository.UserMeditationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class MedtitationAlbumService {
    private final ProgramCommons programCommons;
    private final UserService userService;
    private final UserMeditationRepository userMeditationRepository;
    private final MeditationAlbumRepository meditationAlbumRepository;
    private final MeditationAlbumMapper meditationAlbumMapper;
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UUID createAlbum(UserDetails userDetails,
                                       MeditationAlbumRequest meditationAlbumRequest) {
        return createNewAlbum(userDetails, meditationAlbumRequest).getId();
    }
    public UserMeditationAlbumEntity createNewAlbum(UserDetails userDetails,
                                                    MeditationAlbumRequest meditationAlbumRequest) {
        UserEntity userEntity = userService.getUserByEmail(userDetails.getUsername());
        checkUserTitleAlbums(userDetails, meditationAlbumRequest.getTitle());

        List<UserMeditationEntity> userAlbumMeditationEntities = programCommons.getAlbumMeditationsByIds(
                meditationAlbumRequest.getMeditations(),
                userMeditationRepository
        );

        UserMeditationAlbumEntity entity = meditationAlbumMapper.meditationAlbumRequestToMeditationAlbumEntity(meditationAlbumRequest);
        entity.setUser(userEntity);
        entity.setMeditations(userAlbumMeditationEntities);

        return meditationAlbumRepository.save(entity);
    }
    public MeditationAlbum getAlbum(UserDetails userDetails,
                                    UUID id) {
        UserMeditationAlbumEntity meditationAlbumEntity = programCommons.getAlbumById(id, meditationAlbumRepository);

        if (!programCommons.isUserAdmin(userDetails) && !meditationAlbumEntity.getUser().getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("access deny");
        }

        return meditationAlbumMapper.meditationAlbumEntityToMeditationAlbum(meditationAlbumEntity);
    }
    public Optional<UserMeditationAlbumEntity> getAlbum(String name) {
        return meditationAlbumRepository.findByTitle(name);
    }
    public List<MeditationAlbum> getAllUser(UserDetails userDetails) {
        return meditationAlbumMapper.meditationAlbumEntitiesToMeditationAlbums(
                meditationAlbumRepository.findAllByUser_Email(userDetails.getUsername())
        );
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteAlbumById(UserDetails userDetails, UUID id) {
        UserMeditationAlbumEntity entity = checkControl(userDetails, id);

        if (entity.getTitle().equals("Мои медитации")) {
            throw new IllegalArgumentException("нелья удалить альбом по умолчанию");
        }

        meditationAlbumRepository.delete(entity);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public MeditationAlbum updateAlbum(UserDetails userDetails, UUID id, MeditationAlbumRequest meditationAlbumRequest) {
        UserMeditationAlbumEntity entity = checkControl(userDetails, id);

        List<UserMeditationEntity> userAlbumMeditationEntities = entity.getMeditations();
        if (meditationAlbumRequest.getMeditations() != null) {
            userAlbumMeditationEntities = programCommons.getAlbumMeditationsByIds(
                    meditationAlbumRequest.getMeditations(),
                    userMeditationRepository
            );
        }

        if (meditationAlbumRequest.getTitle() != null) {
            checkUserTitleAlbums(userDetails, meditationAlbumRequest.getTitle());
        }

        MeditationAlbumRequest albumRequest = meditationAlbumMapper.prepareMeditationAlbumRequestFromOldMeditationAlbumEntity(meditationAlbumRequest, entity);

        UserMeditationAlbumEntity updatedEntity = meditationAlbumMapper.meditationAlbumRequestToMeditationAlbumEntity(albumRequest);
        updatedEntity.setUser(userService.getUserByEmail(userDetails.getUsername()));
        updatedEntity.setMeditations(userAlbumMeditationEntities);
        updatedEntity.setId(entity.getId());

        return meditationAlbumMapper.meditationAlbumEntityToMeditationAlbum(meditationAlbumRepository.save(updatedEntity));
    }
    public void updateAlbumCheckedMeditations(UserDetails userDetails, UUID id, List<UserMeditationEntity> userMeditationEntities) {
        UserMeditationAlbumEntity entity = checkControl(userDetails, id);

        List<UserMeditationEntity> userAlbumMeditationEntities = entity.getMeditations();
        if (userMeditationEntities != null) {
            userAlbumMeditationEntities = userMeditationEntities;
        }

        entity.setMeditations(userAlbumMeditationEntities);
        meditationAlbumRepository.save(entity);
    }
    private UserMeditationAlbumEntity checkControl(UserDetails userDetails, UUID id) {
        UserMeditationAlbumEntity entity = programCommons.getAlbumById(id, meditationAlbumRepository);

        if (!programCommons.isUserAdmin(userDetails) && !entity.getUser().getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("Access deny");
        }

        return entity;
    }
    private void checkUserTitleAlbums(UserDetails userDetails, String title) {
        List<UserMeditationAlbumEntity> albums = meditationAlbumRepository.findAllByUser_Email(
                userDetails.getUsername()
        );

        if (albums.stream().anyMatch(el -> el.getTitle().equals(title))) {
            throw new IllegalArgumentException("album with this name already existed");
        }
    }
}
