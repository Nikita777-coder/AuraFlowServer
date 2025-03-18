package app.service;

import app.dto.meditation.GeneratedMeditation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMeditationService {
    public GeneratedMeditation generatedMeditation(String text) {
        List<String> topics = extractMeditationThemesFromText(text);
        throw new RuntimeException();
    }

    private List<String> extractMeditationThemesFromText(String text) {
        throw new RuntimeException();
    }
}
