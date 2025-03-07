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
        return meditationService.getAll();
    }

    @GetMapping("/recommended")
    public List<Meditation> getNewServiceMeditations() {
        return meditationService.getNewMeditations();
    }

    // ADMIN
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UUID uploadNewMeditation(@AuthenticationPrincipal UserDetails userDetails,
                                    @Valid @RequestBody MeditationUploadBodyRequest meditationUploadBodyRequest) {
        return meditationService.uploadMeditation(userDetails, meditationUploadBodyRequest);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteMeditation(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam UUID id) {
        meditationService.delete(userDetails, id);
    }
}
