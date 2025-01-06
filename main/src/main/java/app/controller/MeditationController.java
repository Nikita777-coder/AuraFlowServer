package app.controller;

import app.dto.meditation.Meditation;
import app.dto.meditation.MeditationRequest;
import app.dto.meditation.ModelMeditationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meditation")
public class MeditationController {
    private final MeditationService meditationService;
    @GetMapping
    public List<Meditation> getAllMeditations() {
        return meditationService.getAll(currentUser);
    }

    @GetMapping("/recommended")
    public List<Meditation> getRecommendedMeditations(@AuthenticationPrincipal UserDetails currentUser) {
        return meditationService.getRecommended(currentUser);
    }

    @GetMapping("/new")
    public List<Meditation> getNewServiceMeditations() {
        return meditationService.getNewMeditations();
    }

    @GetMapping("/model-recommended")
    public List<Meditation> getModelRecommendedMeditations(@AuthenticationPrincipal UserDetails currentUser) {
        return meditationService.getModelRecommended(currentUser);
    }

    @PostMapping("/generate")
    public Meditation generateNewMeditation(@AuthenticationPrincipal UserDetails currentUser,
                                            @RequestBody ModelMeditationRequest modelMeditationRequest) {
        return meditationService.generateNewMeditation(
                currentUser,
                modelMeditationRequest
        );
    }

    @PostMapping
    public Meditation addMeditationFromCollection(@AuthenticationPrincipal UserDetails currentUser,
                                                  @RequestBody String meditationLink) {
        return meditationService.addMeditationToUser(currentUser, meditationLink);
    }

    @GetMapping("/user")
    public List<Meditation> getUserMeditations(@AuthenticationPrincipal UserDetails currentUser,
                                               @RequestBody MeditationRequest meditationRequest) {
        return meditationService.generateNewMeditation(
                currentUser,
                meditationRequest
        );
    }
}
