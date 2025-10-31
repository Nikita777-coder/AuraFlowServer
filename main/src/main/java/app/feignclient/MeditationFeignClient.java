package app.feignclient;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@FeignClient(
        value = "meditation-videostorage-client",
        url = "${server.integration.base-url}\\${server.integration.video-storage.uri}"
)
public interface MeditationFeignClient {
    @PostMapping( "${server.integration.video-storage.upload-video-variant}")
    @Headers(HttpHeaders.CONTENT_TYPE + ": " + MediaType.MULTIPART_FORM_DATA_VALUE)
    UUID postMeditationByUploadVideo(
            String title,
            MultipartFile file,
            String description
    );
}
