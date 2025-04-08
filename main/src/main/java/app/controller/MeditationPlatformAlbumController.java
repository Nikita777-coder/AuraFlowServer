package app.controller;

import app.dto.meditationalbum.MeditationAlbumPlatform;
import app.dto.meditationalbum.MeditationAlbumRequest;
import app.service.MeditationPlatformAlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/platform-meditation-album")
@RequiredArgsConstructor
public class MeditationPlatformAlbumController {
    private final MeditationPlatformAlbumService meditationPlatformAlbumService;
    @PostMapping
    @ResponseBody
    public UUID createMeditationPlatformAlbum(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MeditationAlbumRequest meditationAlbumUploadRequest
    ) {
        return meditationPlatformAlbumService.createPlatformAlbum(userDetails, meditationAlbumUploadRequest);
    }
    @GetMapping
    @ResponseBody
    public MeditationAlbumPlatform getMeditationPlatformAlbum(@RequestParam UUID id) {
        return meditationPlatformAlbumService.getPlatformAlbum(id);
    }
    @GetMapping("/all")
    @ResponseBody
    public List<MeditationAlbumPlatform> getMeditationPlatformAlbums() {
        return meditationPlatformAlbumService.getAllServiceAlbums();
    }
    @DeleteMapping
    public void deletePlatformAlbum(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam UUID id) {
        meditationPlatformAlbumService.deletePlatformAlbumById(userDetails, id);
    }
    @PatchMapping
    public MeditationAlbumPlatform updatePlatformAlbum(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestParam UUID id,
                                                       @Valid @RequestBody MeditationAlbumRequest meditationAlbumRequest) {
        return meditationPlatformAlbumService.updatePlatformAlbum(userDetails, id, meditationAlbumRequest);
    }
}
