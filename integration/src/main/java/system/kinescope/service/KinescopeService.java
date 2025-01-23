package system.kinescope.service;

import system.kinescope.dto.KinescopeUploadRequest;
import system.kinescope.dto.KinescopeUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KinescopeService {
    private final RestService restService;

    @Value("kinescope.load-video-url")
    private String kinescopeLoadVideoUrl;

    @Value("kinescope.token")
    private String kinescopeToken;

    @Value("kinescope.project-id")
    private String kinescopeProjectId;
    public KinescopeUploadResponse upload(KinescopeUploadRequest kinescopeUploadRequest) {
        if (kinescopeUploadRequest.getUploadVideo() == null && kinescopeUploadRequest.getSourceLink() == null) {
            throw new IllegalArgumentException("you must fill meditation from local storage or provide link to it");
        }

        Map<String, String> headers = getDefaultHeaders();

        headers.put("X-Video-Title", kinescopeUploadRequest.getTitle());
        return restService.post(kinescopeLoadVideoUrl, headers);
    }
    private Map<String, String> getDefaultHeaders() {
        return new HashMap<>() {{
                put("X-Parent-ID", kinescopeProjectId);
                put("Bearer Token", kinescopeToken);
        }};
    }
}
