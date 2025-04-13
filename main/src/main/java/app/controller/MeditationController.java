package app.controller;

import app.dto.meditation.Meditation;
import app.dto.meditation.MeditationUpdateRequest;
import app.dto.meditation.MeditationUploadBodyRequest;
import app.dto.meditation.Tag;
import app.service.MeditationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meditation")
public class MeditationController {
    private final MeditationService meditationService;
    @GetMapping("/all")
    public List<Meditation> getAllMeditations() {
        return meditationService.getAll();
    }

    @GetMapping("/new")
    public List<Meditation> getNewServiceMeditations() {
        return meditationService.getNewMeditations();
    }

    // ADMIN
    @PostMapping("by-url")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UUID uploadNewMeditation(@AuthenticationPrincipal UserDetails userDetails,
                                    @Valid @RequestBody MeditationUploadBodyRequest meditationUploadBodyRequest) {
        return meditationService.uploadMeditationByUrl(userDetails, meditationUploadBodyRequest);
    }

    @GetMapping("/recommended")
    public List<Meditation> getRecommended() {
        return meditationService.getRecommended();
    }

    @PostMapping(
            value = "/by-upload-video",
            consumes = "multipart/form-data"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UUID uploadNewMeditation(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam String title,
                                    @RequestParam("upload-video") MultipartFile file,
                                    @RequestParam(required = false) String description,
                                    @RequestParam(required = false) String author,
                                    @RequestParam(required = false) List<Tag> tags,
                                    @RequestParam(name = "need-to-promote", required = false) boolean isPromoted) {
        return meditationService.uploadMeditationByUploadVideo(
                userDetails, file, title, description, author, tags, isPromoted
        );
    }

//    @DeleteMapping
//    @ResponseStatus(HttpStatus.OK)
//    public void deleteMeditation(@AuthenticationPrincipal UserDetails userDetails,
//                                 @RequestParam UUID id) {
//        meditationService.delete(userDetails, id);
//    }
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Meditation update(@AuthenticationPrincipal UserDetails userDetails,
                             @Valid @RequestBody MeditationUpdateRequest meditationUpdateRequest) {
        return meditationService.update(
                userDetails,
                meditationUpdateRequest
        );
    }
}
