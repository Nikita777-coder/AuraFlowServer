package app.service;


import app.extra.AuthorizedRequestBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientRestService {
    private final WebClient webClient;
    private final AuthorizedRequestBuilder authorizedRequestBuilder;

    public <T> T get(String baseUrl, String uri, Map<String, String> params, Class<T> tClass) {
        String fullUri = UriComponentsBuilder.fromUriString(baseUrl + uri)
                .queryParams(CollectionUtils.toMultiValueMap(
                        params.entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        e -> List.of(e.getValue())
                                ))
                ))
                .build()
                .toUriString();

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create(fullUri)).build();
        ClientRequest authorizedRequest = authorizedRequestBuilder.withAuthHeaders(request);

        return webClient
                .method(authorizedRequest.method())
                .uri(authorizedRequest.url())
                .headers(headers -> headers.addAll(authorizedRequest.headers()))
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

    public <T> T get(String baseUrl, String uri, ParameterizedTypeReference<T> type) {
        String fullUri = UriComponentsBuilder.fromUriString(baseUrl + uri)
                .build()
                .toUriString();

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create(fullUri)).build();
        ClientRequest authorizedRequest = authorizedRequestBuilder.withAuthHeaders(request);

        return webClient
                .method(authorizedRequest.method())
                .uri(authorizedRequest.url())
                .headers(headers -> headers.addAll(authorizedRequest.headers()))
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
                .bodyToMono(type)
                .block();
    }

    public <T> T post(String baseUrl, String uri, Class<T> tClass) {
        ClientRequest request = ClientRequest.create(HttpMethod.POST, URI.create(baseUrl + uri))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        ClientRequest authorizedRequest = authorizedRequestBuilder.withAuthHeaders(request);

        return webClient
                .method(authorizedRequest.method())
                .uri(authorizedRequest.url())
                .headers(h -> h.addAll(authorizedRequest.headers()))
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


    public <T, R> T post(String baseUrl, String uri, R body, Class<T> tClass) {
        ClientRequest request = ClientRequest.create(HttpMethod.POST, URI.create(baseUrl + uri))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(body))
                .build();

        ClientRequest authorizedRequest = authorizedRequestBuilder.withAuthHeaders(request);

        return webClient
                .method(authorizedRequest.method())
                .uri(authorizedRequest.url())
                .headers(headers -> headers.addAll(authorizedRequest.headers()))
                .body(authorizedRequest.body())
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
        var bodyInserter = BodyInserters.fromMultipartData("title", title)
                .with("upload-video", file.getResource());

        if (description != null) {
            bodyInserter = bodyInserter.with("description", description);
        }

        ClientRequest request = ClientRequest.create(HttpMethod.POST, URI.create(baseUrl + uri))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(bodyInserter)
                .build();

        ClientRequest authorizedRequest = authorizedRequestBuilder.withAuthHeaders(request);

        return webClient
                .method(authorizedRequest.method())
                .uri(authorizedRequest.url())
                .headers(headers -> headers.addAll(authorizedRequest.headers()))
                .body(authorizedRequest.body())
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

    public void delete(String baseUrl, String uri, Map<String, String> params) {
        String fullUri = UriComponentsBuilder.fromUriString(baseUrl + uri)
                .queryParams(CollectionUtils.toMultiValueMap(
                        params.entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        e -> List.of(e.getValue())
                                ))
                ))
                .build()
                .toUriString();

        ClientRequest request = ClientRequest.create(HttpMethod.DELETE, URI.create(fullUri))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        ClientRequest authorizedRequest = authorizedRequestBuilder.withAuthHeaders(request);

        webClient
                .method(authorizedRequest.method())
                .uri(authorizedRequest.url())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {})
                .block();
    }
}

