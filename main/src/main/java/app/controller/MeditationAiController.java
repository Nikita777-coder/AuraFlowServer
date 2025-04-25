package app.controller;

import app.dto.meditation.GeneratedMeditation;
import app.dto.meditation.ModelMeditationRequest;
import app.service.MeditationAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/meditation-generate")
@RequiredArgsConstructor
public class MeditationAiController {
    private final MeditationAiService meditationAiService;
    @PostMapping
    @ResponseBody
    public UUID generateNewMeditation(@AuthenticationPrincipal UserDetails currentUser,
                                      @Valid @RequestBody ModelMeditationRequest modelMeditationRequest) {
        return meditationAiService.generatedMeditation(
                currentUser,
                modelMeditationRequest
        );
    }

    @GetMapping
    @ResponseBody
    public GeneratedMeditation getGeneratedMeditationData(@RequestParam UUID id) {
        return meditationAiService.getMeditation(id);
    }
}
