package system.kinescope.service;

import reactor.core.publisher.Mono;
import system.kinescope.dto.KinescopeUploadRequest;
import system.kinescope.dto.KinescopeUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import system.kinescope.dto.KinescopeVideoData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KinescopeService {
    private final RestService restService;

    @Value("kinescope.load-video-url")
    private String kinescopeLoadVideoUrl;

    @Value("kinescope.get-video-url")
    private String kinescopeGetVideoUrl;

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
    public Mono<KinescopeVideoData> get(UUID videoId) {
        Map<String, String> headers = getDefaultHeaders();

        String fullUrl = kinescopeGetVideoUrl + String.format("/%s", videoId);
        return restService.get(fullUrl, headers);
    }
    public void delete(UUID videoId) {
        Map<String, String> headers = getDefaultHeaders();

        String fullUrl = kinescopeGetVideoUrl + String.format("/%s", videoId);
        restService.delete(fullUrl, headers);
    }
    private Map<String, String> getDefaultHeaders() {
        return new HashMap<>() {{
                put("X-Parent-ID", kinescopeProjectId);
                put("Bearer Token", kinescopeToken);
        }};
    }
}
