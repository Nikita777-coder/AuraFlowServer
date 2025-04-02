package app.controller;

import app.dto.meditationalbum.MeditationAlbum;
import app.dto.meditationalbum.MeditationAlbumRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meditation-album")
@RequiredArgsConstructor
public class MeditationAlbumController {
    private final MedtitationAlbumService medtitationAlbumService;

    @PostMapping
    @ResponseBody
    public MeditationAlbum createMeditationAlbum(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MeditationAlbumRequest meditationAlbumUploadRequest
    ) {
        return medtitationAlbumService.createAlbum(userDetails, meditationAlbumUploadRequest);
    }

    @GetMapping
    @ResponseBody
    public MeditationAlbum getMeditationAlbum(@RequestParam UUID id) {
        return medtitationAlbumService.getAlbum(id);
    }

    @GetMapping("/all")
    @ResponseBody
    public List<MeditationAlbum> getMeditationAlbums() {
        return medtitationAlbumService.getAllServiceAlbums();
    }

    @DeleteMapping
    @ResponseBody
    public String deleteAlbum(@RequestParam UUID id) {
        medtitationAlbumService.deleteAlbumById(id);
        return "success";
    }

    @PatchMapping
    public MeditationAlbum updateAlbum(@RequestBody MeditationAlbumRequest meditationAlbumRequest) {
        return medtitationAlbumService.updateAlbum(meditationAlbumRequest);
    }
}
