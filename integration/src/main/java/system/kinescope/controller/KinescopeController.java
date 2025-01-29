package system.kinescope.controller;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import system.kinescope.dto.KinescopeUploadRequest;
import system.kinescope.dto.KinescopeUploadResponse;
import system.kinescope.dto.KinescopeVideoData;
import system.kinescope.service.KinescopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/integration/kinescoope/video")
@RequiredArgsConstructor
public class KinescopeController {
    private final KinescopeService kinescopeService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public KinescopeUploadResponse upload(@Valid @RequestBody KinescopeUploadRequest kinescopeUploadRequest) {
        return kinescopeService.upload(kinescopeUploadRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Mono<KinescopeVideoData> get(@RequestParam(name = "video-id") UUID kinescopeVideoId)  {
        return kinescopeService.get(kinescopeVideoId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void delete(@RequestParam(name = "video-id") UUID kinescopeVideoId)  {
        kinescopeService.delete(kinescopeVideoId);
    }
}
