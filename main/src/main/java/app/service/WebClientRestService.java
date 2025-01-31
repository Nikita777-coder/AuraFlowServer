package app.service;


import app.service.authservice.KeycloakAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientRestService {
    private final WebClient webClient;
    private final KeycloakAuthService keycloakAuthService;

    public <T> T get(String url, Map<String, String> params, Class<T> tClass) {
        String token = getToken();

        Mono<T> response = webClient
                .get()
                .uri(uriBuilder -> {
                    params.forEach(uriBuilder::queryParam);
                    return uriBuilder.path(url).build();
                })
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(tClass);

        return response.block();
    }

    public <T, R> T post(String uri, R body, Class<T> tClass) {
        String token = getToken();

        return webClient
                .post()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(tClass)
                .block();
    }

    public void delete(String url, Map<String, String> params) {
        webClient
                .delete()
                .uri(uriBuilder -> {
                    params.forEach(uriBuilder::queryParam);
                    return uriBuilder.path(url).build();
                })
                .header("Authorization", "Bearer " + getToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {})
                .block();
    }

    private String getToken() {
        AccessTokenResponse authResponse = keycloakAuthService.getAccessToken();
        return authResponse.getToken();
    }
}

