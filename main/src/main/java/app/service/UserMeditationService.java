package app.service;

import app.dto.meditation.GeneratedMeditation;
import app.dto.meditation.MeditationRequest;
import app.dto.meditation.UserMeditationUpdateRequest;
import app.dto.meditation.UserMeditation;
import app.dto.meditationalbum.MeditationAlbumRequest;
import app.entity.usermeditation.UserMeditationEntity;
import app.extra.ProgramCommons;
import app.mapper.UserMeditationMapper;
import app.repository.MeditationRepository;
import app.repository.UserMeditationRepository;
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
public class UserMeditationService {
    private final ProgramCommons programCommons;
    private final MeditationRepository meditationRepository;
    private final MedtitationAlbumService medtitationAlbumService;
    private final UserMeditationRepository userMeditationRepository;
    private final UserMeditationMapper userMeditationMapper;
    private final UserService userService;

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
        userMeditationEntity = userMeditationRepository.save(userMeditationEntity);

        var album = medtitationAlbumService.getAlbum("Мои медитации").orElseGet(() -> {
                    MeditationAlbumRequest meditationAlbumRequest = new MeditationAlbumRequest();
                    meditationAlbumRequest.setTitle("Мои медитации");
                    meditationAlbumRequest.setDescription("коллекция всех медитаций, добавленных вами из базы медитаций " +
                            "сервиса или сгенерированных вами с помощью модели");

                    return medtitationAlbumService.createNewAlbum(userDetails, meditationAlbumRequest);
                }
        );

        List<UserMeditationEntity> albumMeditations = album.getMeditations();
        albumMeditations.add(userMeditationEntity);
        medtitationAlbumService.updateAlbumCheckedMeditations(userDetails, album.getId(), albumMeditations);

        return userMeditationEntity.getId();
    }
    public List<UserMeditation> getUserAll(UserDetails userDetails, MeditationRequest meditationRequest) {
        return userMeditationMapper.userMeditationEntitiesToUserMeditations(
                userMeditationRepository.findAllByUser_EmailAndStatuses(
                        userDetails.getUsername(),
                        meditationRequest.getStatuses()
                )
        );
    }
    public UserMeditation update(UserDetails userDetails, UserMeditationUpdateRequest meditationUpdateRequest) {
        UserMeditationEntity userMeditationEntity = checkAccessAndGet(meditationUpdateRequest.getId(), userDetails);

        userMeditationEntity = userMeditationMapper.updateEntity(meditationUpdateRequest, userMeditationEntity);
        return userMeditationMapper.userMeditationEntityToUserMeditation(userMeditationRepository.save(userMeditationEntity));
    }
    public void delete(UserDetails userDetails, UUID id) {
        userMeditationRepository.delete(checkAccessAndGet(id, userDetails));
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
