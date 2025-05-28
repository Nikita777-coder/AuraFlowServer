package app.service;


import app.service.authservice.KeycloakAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
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
                .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> {
                                System.out.println("Error Response Body get 400: " + responseBody);
                                return Mono.error(new IllegalArgumentException(responseBody));
                            });
                })
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> {
                                System.out.println("Error Response Body get 500: " + responseBody);
                                return Mono.error(new IllegalArgumentException(responseBody));
                            });
                })
                .bodyToMono(tClass);

        var res = response.block();

        return res;
    }

    public <T> T get(String baseUrl, String uri, ParameterizedTypeReference<T> typeReference) {
//        String token = getToken();

        Mono<T> response = webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
//                .header("Authorization", "Bearer " + token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> {
                                System.out.println("Error Response Body get 400: " + responseBody);
                                return Mono.error(new IllegalArgumentException(responseBody));
                            });
                })
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> {
                                System.out.println("Error Response Body get 500: " + responseBody);
                                return Mono.error(new IllegalArgumentException(responseBody));
                            });
                })
                .bodyToMono(typeReference);

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
                .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> {
                                System.out.println("Error Response Body post 400: " + responseBody);
                                return Mono.error(new IllegalArgumentException(responseBody));
                            });
                })
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(responseBody -> {
                        System.out.println("Error Response Body post 500 " + responseBody);
                        return Mono.error(new IllegalArgumentException(responseBody));
                    });
        })
                .bodyToMono(tClass)
                .block();

        return ans;
    }
    public <T> T post(String baseUrl, String uri, Class<T> tClass) {
        return webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri(uri)
                //                .header("Authorization", "Bearer " + token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> {
                                System.out.println("Error Response Body post 400: " + responseBody);
                                return Mono.error(new IllegalArgumentException(responseBody));
                            });
                })
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> {
                                System.out.println("Error Response Body post 500: " + responseBody);
                                return Mono.error(new IllegalArgumentException(responseBody));
                            });
                })
                .bodyToMono(tClass)
                .block();
    }

    public <T> T postVideo(String baseUrl, String uri, String title, MultipartFile file, String description, Class<T> tClass) {
//        String token = getToken();

        var bodyInserter = BodyInserters.fromMultipartData("title", title)
                .with("upload-video", file.getResource());

        if (description != null) {
            bodyInserter = bodyInserter.with("description", description);
        }

        var ans = webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri(uri)
//                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(bodyInserter)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> {
                                System.out.println("Error Response Body postVideo 400: " + responseBody);
                                return Mono.error(new IllegalArgumentException(responseBody));
                            });
                })
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> {
                                System.out.println("Error Response Body postVideo 500: " + responseBody);
                                return Mono.error(new IllegalArgumentException(responseBody));
                            });
                })
                .bodyToMono(tClass)
                .block();

        return ans;
    }

    public void delete(String baseUrl, String uri) {
        webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .delete()
                .uri(uri)
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

