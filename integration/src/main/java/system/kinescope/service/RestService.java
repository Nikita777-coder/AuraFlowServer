package system.kinescope.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestService {
    private final WebClient webClient;
    public <T> T post(String url, Map<String, String> headers) {
        return webClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setAll(headers))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<T>() {})
                .block();
    }

    public <T> Mono<T> get(String uri, Map<String, String> headers) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(uri).build())
                .headers(headers1 -> headers1.setAll(headers))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<T>() {});
    }

    public void delete(String url, Map<String, String> headers) {
        webClient
                .delete()
                .uri(url)
                .headers(headers1 -> headers1.setAll(headers))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {})
                .block();
    }
}
