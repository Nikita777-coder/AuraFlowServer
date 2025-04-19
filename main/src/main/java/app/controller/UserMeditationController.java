package app.controller;

import app.dto.meditation.*;
import app.entity.usermeditation.StatusEntity;
import app.service.UserMeditationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-meditations")
@RequiredArgsConstructor
public class UserMeditationController {
    private final UserMeditationService userMeditationService;
    @PostMapping("/add")
    @ResponseBody
    public UUID addMeditationFromCollection(@AuthenticationPrincipal UserDetails currentUser,
                                                  @RequestBody UserMeditationUploadRequest userMeditationUploadRequest) {
        return userMeditationService.addMeditationToUser(currentUser, userMeditationUploadRequest);
    }
    @GetMapping("/all")
    @ResponseBody
    public List<UserMeditation> getUserMeditations(@AuthenticationPrincipal UserDetails currentUser
//                                                   @RequestParam  List<Status> statuses
                                                   ) {
        return userMeditationService.getUserAll(
                currentUser,
                new ArrayList<>()
        );
    }

    @PostMapping("/generate")
    @ResponseBody
    public GeneratedMeditation generateNewMeditation(@AuthenticationPrincipal UserDetails currentUser,
                                                     @Valid @RequestBody ModelMeditationRequest modelMeditationRequest) {
        return userMeditationService.generatedMeditation(
                currentUser,
                modelMeditationRequest
        );
    }

    @PatchMapping
    @ResponseBody
    public UserMeditation update(@AuthenticationPrincipal UserDetails currentUser,
                             @Valid @RequestBody UserMeditationUpdateRequest updateMeditationData) {
        return userMeditationService.update(currentUser, updateMeditationData);
    }
    @DeleteMapping
    public void delete(@AuthenticationPrincipal UserDetails currentUser,
                       @RequestParam UUID id) {
        userMeditationService.delete(currentUser, id);
    }
}
