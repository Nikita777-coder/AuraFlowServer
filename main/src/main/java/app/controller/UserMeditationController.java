package app.controller;

import app.dto.meditation.GeneratedMeditation;
import app.dto.meditation.Meditation;
import app.dto.meditation.MeditationRequest;
import app.dto.meditation.ModelMeditationRequest;
import app.service.UserMeditationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-meditations")
@RequiredArgsConstructor
public class UserMeditationController {
    private final UserMeditationService userMeditationService;
    @PostMapping("/add")
    @ResponseBody
    public Meditation addMeditationFromCollection(@AuthenticationPrincipal UserDetails currentUser,
                                                  @RequestParam UUID meditationId) {
        return userMeditationService.addMeditationToUser(currentUser, meditationId);
    }
//    @GetMapping("/all")
//    @ResponseBody
//    public List<Meditation> getUserMeditations(@AuthenticationPrincipal UserDetails currentUser,
//                                               @RequestBody MeditationRequest meditationRequest) {
//        return userMeditationService.getUserAll(
//                currentUser,
//                meditationRequest
//        );
//    }
//
//    @PostMapping("/generate")
//    @ResponseBody
//    public GeneratedMeditation generateNewMeditation(@AuthenticationPrincipal UserDetails currentUser,
//                                                     @RequestBody ModelMeditationRequest modelMeditationRequest) {
//        return userMeditationService.generateNewMeditation(
//                currentUser,
//                modelMeditationRequest
//        );
//    }
//
//    @GetMapping("/specially-selected")
//    @ResponseBody
//    public List<Meditation> getRecommendedMeditations(@AuthenticationPrincipal UserDetails currentUser) {
//        return userMeditationService.getRecommended(currentUser);
//    }
//    @PatchMapping
//    @ResponseBody
//    public Meditation update(@AuthenticationPrincipal UserDetails currentUser,
//                             @RequestBody MeditationRequest updateMeditationData) {
//        return userMeditationService.update(currentUser, updateMeditationData);
//    }
//    @DeleteMapping
//    public void delete(@AuthenticationPrincipal UserDetails currentUser,
//                       @RequestParam UUID id) {
//        userMeditationService.delete(currentUser, id);
//    }
}
