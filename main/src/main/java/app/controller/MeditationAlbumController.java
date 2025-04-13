package app.controller;

import app.dto.meditationalbum.MeditationAlbum;
import app.dto.meditationalbum.MeditationAlbumRequest;
import app.service.MedtitationAlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-meditation-album")
@RequiredArgsConstructor
public class MeditationAlbumController {
    private final MedtitationAlbumService medtitationAlbumService;

    @PostMapping
    @ResponseBody
    public UUID createMeditationAlbum(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MeditationAlbumRequest meditationAlbumUploadRequest
    ) {
        return medtitationAlbumService.createAlbum(userDetails, meditationAlbumUploadRequest);
    }

    @GetMapping
    @ResponseBody
    public MeditationAlbum getMeditationAlbum(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestParam UUID id) {
        return medtitationAlbumService.getAlbum(userDetails, id);
    }

    @GetMapping("/all")
    @ResponseBody
    public List<MeditationAlbum> getMeditationAlbums(@AuthenticationPrincipal UserDetails userDetails) {
        return medtitationAlbumService.getAllUser(userDetails);
    }

    @DeleteMapping
    public void deleteAlbum(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam UUID id) {
        medtitationAlbumService.deleteAlbumById(userDetails, id);
    }

    @PatchMapping
    public MeditationAlbum updateAlbum(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestParam UUID id,
                                       @Valid @RequestBody MeditationAlbumRequest meditationAlbumRequest) {
        return medtitationAlbumService.updateAlbum(userDetails, id, meditationAlbumRequest);
    }
}
