package app.service;

import app.dto.meditation.GeneratedMeditation;
import app.dto.meditation.Meditation;
import app.entity.meditation.MeditationEntity;
import app.entity.usermeditation.UserMeditationEntity;
import app.repository.MeditationRepository;
import app.repository.UserMeditationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserMeditationService {
    private final MeditationRepository meditationRepository;
    private final MedtitationAlbumService medtitationAlbumService;
    private final UserMeditationRepository userMeditationRepository;

    public GeneratedMeditation generatedMeditation(String text) {
        List<String> topics = extractMeditationThemesFromText(text);
        throw new RuntimeException();
    }

    public Meditation addMeditationToUser(UserDetails userDetails, UUID meditationId) {
        if (meditationRepository.findById(meditationId).isEmpty()) {
            throw new IllegalArgumentException("no such meditation");
        }

        if (!medtitationAlbumService.getAlbum("Мои медитации").isPresent()) {

        }

        return null;
    }

    private List<String> extractMeditationThemesFromText(String text) {
        throw new RuntimeException();
    }
}
