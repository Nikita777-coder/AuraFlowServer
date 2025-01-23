package kinescope.controller;

import jakarta.validation.Valid;
import kinescope.dto.KinescopeUploadRequest;
import kinescope.service.KinescopeService;
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
    public String upload(@Valid @RequestBody KinescopeUploadRequest kinescopeUploadRequest) {
        return kinescopeService.upload(kinescopeUploadRequest);
    }
}
