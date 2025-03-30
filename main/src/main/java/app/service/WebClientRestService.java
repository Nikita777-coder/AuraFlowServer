package app.service;


import app.service.authservice.KeycloakAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientRestService {
    private final WebClient webClient;
    private final KeycloakAuthService keycloakAuthService;

    public <T> T get(String baseUrl, String uri, Map<String, String> params, Class<T> tClass) {
//        String token = getToken();

        Mono<T> response = webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uriBuilder -> {
                    params.forEach(uriBuilder::queryParam);
                    return uriBuilder.path(uri).build();
                })
//                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(tClass);

        var res = response.block();

        return res;
    }

    public <T, R> T post(String baseUrl, String uri, R body, Class<T> tClass) {
//        String token = getToken();

        var ans = webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri(uri)
//                .header("Authorization", "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(tClass)
                .block();

        return ans;
    }

    public <T> T post(String baseUrl, String uri, Map<String, Object> params, Class<T> tClass) {
//        String token = getToken();

        var ans = webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri(uriBuilder -> {
                    params.forEach(uriBuilder::queryParam);
                    return uriBuilder.path(uri).build();
                })
//                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .retrieve()
                .bodyToMono(tClass)
                .block();

        return ans;
    }

    public void delete(String baseUrl, String uri, Map<String, String> params) {
        webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .delete()
                .uri(uriBuilder -> {
                    params.forEach(uriBuilder::queryParam);
                    return uriBuilder.path(uri).build();
                })
//                .header("Authorization", "Bearer " + getToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {})
                .block();
    }

    private String getToken() {
        AccessTokenResponse authResponse = keycloakAuthService.getAccessToken();
        return authResponse.getToken();
    }
}

