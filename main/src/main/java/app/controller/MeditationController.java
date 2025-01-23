package app.controller;

import app.dto.meditation.Meditation;
import app.dto.meditation.MeditationRequest;
import app.dto.meditation.MeditationUploadBodyRequest;
import app.dto.meditation.ModelMeditationRequest;
import app.service.MeditationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meditation")
public class MeditationController {
    private final MeditationService meditationService;
    @GetMapping
    public List<Meditation> getAllMeditations() {
        throw new RuntimeException();
//        return meditationService.getAll(currentUser);
    }

    @GetMapping("/recommended")
    public List<Meditation> getRecommendedMeditations(@AuthenticationPrincipal UserDetails currentUser) {
        throw new RuntimeException();

//        return meditationService.getRecommended(currentUser);
    }

    @GetMapping("/new")
    public List<Meditation> getNewServiceMeditations() {
        throw new RuntimeException();
//        return meditationService.getNewMeditations();
    }

    @GetMapping("/model-recommended")
    public List<Meditation> getModelRecommendedMeditations(@AuthenticationPrincipal UserDetails currentUser) {
        throw new RuntimeException();
//        return meditationService.getModelRecommended(currentUser);
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

    @PostMapping
    public Meditation addMeditationFromCollection(@AuthenticationPrincipal UserDetails currentUser,
                                                  @RequestBody String meditationLink) {
        throw new RuntimeException();
//        return meditationService.addMeditationToUser(currentUser, meditationLink);
    }

    @GetMapping("/user")
    public List<Meditation> getUserMeditations(@AuthenticationPrincipal UserDetails currentUser,
                                               @RequestBody MeditationRequest meditationRequest) {
        throw new RuntimeException();
//        return meditationService.generateNewMeditation(
//                currentUser,
//                meditationRequest
//        );
    }

    // ADMIN
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UUID uploadNewMeditation(@AuthenticationPrincipal UserDetails userDetails,
                                    @Valid @RequestBody MeditationUploadBodyRequest meditationUploadBodyRequest) {
        throw new RuntimeException();
//        return meditationService.uploadMeditation(userDetails, meditationUploadBodyRequest);
    }
}
