package system.kinescope.controller;

import jakarta.validation.Valid;
import system.kinescope.dto.KinescopeUploadRequest;
import system.kinescope.dto.KinescopeUploadResponse;
import system.kinescope.service.KinescopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/integration/kinescoope")
@RequiredArgsConstructor
public class KinescopeController {
    private final KinescopeService kinescopeService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    // return id of saved video in local db of meditations
    public KinescopeUploadResponse upload(@Valid @RequestBody KinescopeUploadRequest kinescopeUploadRequest) {
        return kinescopeService.upload(kinescopeUploadRequest);
    }
}
