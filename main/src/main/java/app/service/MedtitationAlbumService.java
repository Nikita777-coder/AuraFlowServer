package app.service;

import app.dto.meditationalbum.MeditationAlbum;
import app.dto.meditationalbum.MeditationAlbumPlatform;
import app.dto.meditationalbum.MeditationAlbumRequest;
import app.entity.MeditationAlbumEntity;
import app.entity.UserEntity;
import app.entity.userattributes.Role;
import app.extra.ProgramCommons;
import app.mapper.MeditationMapper;
import app.repository.MeditationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedtitationAlbumService {
    private final MeditationMapper meditationMapper;
    private final ProgramCommons programCommons;
    private final UserService userService;
    private final UserMeditationRepository userMeditationRepository;
    private final MeditationRepository meditationRepository;
    private final MeditationAlbumRepository meditationAlbumRepository;
    private final MeditationAlbumMapper meditationAlbumMapper;
    public UUID createAlbum(UserDetails userDetails,
                                       MeditationAlbumRequest meditationAlbumRequest) {
        UserEntity userEntity = userService.getUserByEmail(userDetails.getUsername());
        checkUserAlbums(userDetails, meditationAlbumRequest.getTitle());

        List<UserMeditationEntity> userAlbumMeditationEntities = getAlbumMeditationsByIds(
                meditationAlbumRequest.getMeditations(),
                programCommons.isUserAdmin(userDetails)
        );

        // ignore user and meditations
        MeditationAlbumEntity entity = meditationAlbumMapper.meditationAlbumRequestToMeditationAlbumEntity(meditationAlbumRequest);
        entity.setUser(userEntity);
        entity.setMeditations(userAlbumMeditationEntities);

        return meditationAlbumRepository.save(entity).getId();
    }
    public MeditationAlbum getAlbum(UserDetails userDetails,
                                    UUID id) {
        MeditationAlbumEntity meditationAlbumEntity = getAlbumById(id);

        if (!programCommons.isUserAdmin(userDetails) && !meditationAlbumEntity.getUser().getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("access deny");
        }

        return meditationAlbumMapper.meditationAlbumEntityToMeditationAlbum(meditationAlbumEntity);
    }
    public MeditationAlbumPlatform getPlatformAlbum(UUID id) {
        MeditationAlbumEntity meditationAlbumEntity = getAlbumById(id);
        return meditationAlbumMapper.meditationAlbumEntityToMeditationAlbumPlatform(meditationAlbumEntity);
    }
    public List<MeditationAlbumPlatform> getAllServiceAlbums(UserDetails userDetails) {
        if (programCommons.isUserAdmin(userDetails)) {
            return meditationAlbumMapper.meditationAlbumEntitiesToMeditationAlbumsPlatform(
                    meditationAlbumRepository.findAllByUserEmail(userDetails.getUsername())
            );
        }

        return meditationAlbumMapper.meditationAlbumEntitiesToMeditationAlbumsPlatform(meditationAlbumRepository.findAllByUserRole(Role.ADMIN));
    }
    public List<MeditationAlbum> getAllUser(UserDetails userDetails) {
        if (programCommons.isUserAdmin(userDetails)) {
            throw new IllegalArgumentException("illegal point usage");
        }

        return meditationAlbumMapper.meditationAlbumEntitiesToMeditationAlbums(
                meditationAlbumRepository.findAllByUserEmail(userDetails)
        );
    }
    public void deleteAlbumById(UserDetails userDetails, UUID id) {
        MeditationAlbumEntity entity = checkControl(userDetails, id);

        meditationAlbumRepository.delete(entity);
    }
    public UUID updateAlbum(UserDetails userDetails, UUID id, MeditationAlbumRequest meditationAlbumRequest) {
        MeditationAlbumEntity entity = checkControl(userDetails, id);

        List<UserMeditationEntity> userAlbumMeditationEntities = getAlbumMeditationsByIds(
                meditationAlbumRequest.getMeditations(),
                programCommons.isUserAdmin(userDetails)
        );

        if (meditationAlbumRequest.getTitle() != null) {
            checkUserAlbums(userDetails, meditationAlbumRequest.getTitle());
        }

        // check null values. if some then from old entity get
        meditationAlbumMapper.prepareMeditationAlbumRequestFromOldMeditationAlbumEntity(meditationAlbumRequest, entity);

        // ignore user and meditations
        MeditationAlbumEntity updatedEntity = meditationAlbumMapper.meditationAlbumRequestToMeditationAlbumEntity(meditationAlbumRequest);
        updatedEntity.setUser(userService.getUserByEmail(userDetails.getUsername()));
        updatedEntity.setMeditations(userAlbumMeditationEntities);
        updatedEntity.setId(entity.getId());

        return meditationAlbumRepository.save(updatedEntity).getId();
    }
    private MeditationAlbumEntity getAlbumById(UUID id) {
        return meditationAlbumRepository.findById(id).orElseThrow(new IllegalArgumentException("No such album"));
    }
    private List<UserMeditationEntity> getAlbumMeditationsByIds(List<UUID> ids, boolean isUserAdmin) {
        List<UserMeditationEntity> userMeditationEntities = new ArrayList<>(ids.size());

        for (UUID id : ids) {
            if (isUserAdmin) {
                userMeditationEntities.add(
                      meditationMapper.meditationEntityToUserMeditationEntity(
                        meditationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(String.format("no meditation by id %s", id))
                      ))
                );
            } else {
                userMeditationEntities.add(
                        userMeditationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(String.format("no meditation by id %s", id)))
                );
            }
        }

        return userMeditationEntities;
    }
    private MeditationAlbumEntity checkControl(UserDetails userDetails, UUID id) {
        MeditationAlbumEntity entity = getAlbumById(id);

        if ((entity.getUser().getRole().equals(Role.USER) && !programCommons.isUserAdmin(userDetails)
                && !entity.getUser().getEmail().equals(userDetails.getUsername())
        ) || (entity.getUser().getRole().equals(Role.ADMIN) && !entity.getUser().getEmail().equals(userDetails.getUsername()))) {
            throw new AccessDeniedException("Access deny");
        }

        return entity;
    }
    private void checkUserAlbums(UserDetails userDetails, String title) {
        List<MeditationAlbumEntity> albums = meditationAlbumRepository.findAllByUser(
                userService.getUserByEmail(userDetails.getUsername())
        );

        if (programCommons.isUserAdmin(userDetails)) {
            albums = meditationAlbumRepository.findAllByUserRole(Role.ADMIN);
        }

        if (albums.stream().anyMatch(el -> el.getTitle().equals(title))) {
            throw new IllegalArgumentException("album with this name already existed");
        }
    }
}
