package kinescope.service;

import kinescope.dto.KinescopeUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    public String upload(KinescopeUploadRequest kinescopeUploadRequest) {
        Map<String, String> headers = getDefaultHeaders();

        headers.put("X-Video-Title", kinescopeUploadRequest.)
        return restService.post(kinescopeLoadVideoUrl, headers);
    }
    private Map<String, String> getDefaultHeaders() {
        return Map.of(
                "X-Parent-ID", kinescopeProjectId,
                "Bearer Token", kinescopeToken
                );
    }
}
