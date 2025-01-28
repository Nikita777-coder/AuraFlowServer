package system.kinescope.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestService {
    private final WebClient webClient;
    public <T> T post(String url, Map<String, String> headers) {
        throw new RuntimeException();
    }
}
