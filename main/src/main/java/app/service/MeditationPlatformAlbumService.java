package app.service;

import app.dto.meditationalbum.MeditationAlbumPlatform;
import app.dto.meditationalbum.MeditationAlbumRequest;
import app.entity.MeditationPlatformAlbumEntity;
import app.entity.UserEntity;
import app.entity.meditation.MeditationEntity;
import app.extra.ProgramCommons;
import app.mapper.MeditationPlatformAlbumMapper;
import app.repository.MeditationPlatformAlbumRepository;
import app.repository.MeditationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class MeditationPlatformAlbumService {
    private final ProgramCommons programCommons;
    private final UserService userService;
    private final MeditationRepository meditationRepository;
    private final MeditationPlatformAlbumRepository meditationPlatformAlbumRepository;
    private final MeditationPlatformAlbumMapper meditationPlatformAlbumMapper;
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UUID createPlatformAlbum(UserDetails userDetails,
                                    MeditationAlbumRequest meditationAlbumRequest) {
        UserEntity userEntity = userService.getUserByEmail(userDetails.getUsername());
        checkPlatformTitleAlbums(meditationAlbumRequest.getTitle());

        List<MeditationEntity> platformMeditations = programCommons.getAlbumMeditationsByIds(
                meditationAlbumRequest.getMeditations(),
                meditationRepository
        );

        MeditationPlatformAlbumEntity meditationPlatformAlbumEntity = meditationPlatformAlbumMapper
                .meditationAlbumRequestToMeditationPlatformAlbumEntity(meditationAlbumRequest);
        meditationPlatformAlbumEntity.setUserEntity(userEntity);
        meditationPlatformAlbumEntity.setMeditationsFromPlatform(platformMeditations);

        return meditationPlatformAlbumRepository.save(meditationPlatformAlbumEntity).getId();
    }
    public MeditationAlbumPlatform getPlatformAlbum(UUID id) {
        MeditationPlatformAlbumEntity meditationAlbumEntity = programCommons.getAlbumById(id, meditationPlatformAlbumRepository);
        return meditationPlatformAlbumMapper.meditationPlatformAlbumEntityToMeditationAlbumPlatform(meditationAlbumEntity);
    }
    public List<MeditationAlbumPlatform> getAllServiceAlbums() {
        return meditationPlatformAlbumMapper.meditationPlatformAlbumEntitiesToMeditationAlbumsPlatform(meditationPlatformAlbumRepository.findAll());
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deletePlatformAlbumById(UserDetails userDetails, UUID id) {
        MeditationPlatformAlbumEntity entity = getPlatformAlbumWithAdminCheck(id, userDetails);

        List<MeditationEntity> albums = entity.getMeditationsFromPlatform();

        for (MeditationEntity album : albums) {
            album.getAlbumEntities().remove(entity);
        }

        meditationRepository.saveAll(albums);
        meditationPlatformAlbumRepository.delete(entity);
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public MeditationAlbumPlatform updatePlatformAlbum(UserDetails userDetails, UUID id, MeditationAlbumRequest meditationAlbumRequest) {
        MeditationPlatformAlbumEntity entity = getPlatformAlbumWithAdminCheck(id, userDetails);

        List<MeditationEntity> userAlbumMeditationEntities = entity.getMeditationsFromPlatform();
        if (meditationAlbumRequest.getMeditations() != null) {
            userAlbumMeditationEntities = programCommons.getAlbumMeditationsByIds(
                    meditationAlbumRequest.getMeditations(),
                    meditationRepository
            );
        }

        if (meditationAlbumRequest.getTitle() != null) {
            checkPlatformTitleAlbums(meditationAlbumRequest.getTitle());
        }

        MeditationAlbumRequest albumRequest = meditationPlatformAlbumMapper.prepareMeditationAlbumRequestFromOldMeditationPlatformAlbumEntity(meditationAlbumRequest, entity);

        MeditationPlatformAlbumEntity updatedEntity = meditationPlatformAlbumMapper.meditationAlbumRequestToMeditationPlatformAlbumEntity(albumRequest);
        updatedEntity.setUserEntity(userService.getUserByEmail(userDetails.getUsername()));
        updatedEntity.setMeditationsFromPlatform(userAlbumMeditationEntities);
        updatedEntity.setId(entity.getId());

        return meditationPlatformAlbumMapper.meditationPlatformAlbumEntityToMeditationAlbumPlatform(meditationPlatformAlbumRepository.save(updatedEntity));
    }
    private MeditationPlatformAlbumEntity getPlatformAlbumWithAdminCheck(UUID id, UserDetails userDetails) {
        if (!programCommons.isUserAdmin(userDetails)) {
            throw new AccessDeniedException("Access deny");
        }

        return programCommons.getAlbumById(id, meditationPlatformAlbumRepository);
    }
    private void checkPlatformTitleAlbums(String title) {
        List<MeditationPlatformAlbumEntity> albums = meditationPlatformAlbumRepository.findAll();

        if (albums.stream().anyMatch(el -> el.getTitle().equals(title))) {
            throw new IllegalArgumentException("album with this name already existed");
        }
    }
}
