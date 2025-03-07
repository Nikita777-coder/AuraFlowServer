package app.service.authservice;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KeycloakAuthService {
    private final WebClient keyCloakWebClient;

    @Value("${server.oauth.grand-type}")
    private String grandType;

    @Value("${server.keycloak.realm}")
    private String realm;

    @Value("${server.keycloak.client-id}")
    private String clientId;

    @Value("${server.keycloak.client-secret}")
    private String clientSecret;

    public AccessTokenResponse getAccessToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", grandType);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        var response = keyCloakWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("realms", realm, "protocol", "openid-connect", "token")
                        .build())
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(AccessTokenResponse.class).block();

        if (response == null) {
            throw new IllegalArgumentException("response of getting access token is null");
        }

        return response;
    }
}

