package app.controller;

import app.dto.meditation.Meditation;
import app.dto.meditation.MeditationRequest;
import app.dto.meditation.ModelMeditationRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-meditations")
public class UserMeditationController {
    @PostMapping("/add")
    public Meditation addMeditationFromCollection(@AuthenticationPrincipal UserDetails currentUser,
                                                  @RequestBody String meditationLink) {
        throw new RuntimeException();
//        return meditationService.addMeditationToUser(currentUser, meditationLink);
    }
    @GetMapping
    public List<Meditation> getUserMeditations(@AuthenticationPrincipal UserDetails currentUser,
                                               @RequestBody MeditationRequest meditationRequest) {
        throw new RuntimeException();
//        return meditationService.generateNewMeditation(
//                currentUser,
//                meditationRequest
//        );
    }

    @PostMapping("/generate")
    public Meditation generateNewMeditation(@AuthenticationPrincipal UserDetails currentUser,
                                            @RequestBody ModelMeditationRequest modelMeditationRequest) {
        throw new RuntimeException();
//        return meditationService.generateNewMeditation(
//                currentUser,
//                modelMeditationRequest
//        );
    }

    @GetMapping("/specially-selected")
    public List<Meditation> getRecommendedMeditations(@AuthenticationPrincipal UserDetails currentUser) {
        throw new RuntimeException();

//        return meditationService.getRecommended(currentUser);
    }

    @GetMapping("/model-recommended")
    public List<Meditation> getModelRecommendedMeditations(@AuthenticationPrincipal UserDetails currentUser) {
        throw new RuntimeException();
//        return meditationService.getModelRecommended(currentUser);
    }
}
