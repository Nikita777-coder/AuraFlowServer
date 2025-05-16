package app.configs;

import app.controller.TokenController;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class ZitadelWebClientConfigs {
    private final UserRepository userRepository;
    private final TokenController tokenController;

    @Value("${server.zitadel.id}")
    private String registrationId;
    @Value("${server.oidc.email}")
    private String oidcEmail;
    @Value("${server.zitadel.token-uri}")
    private String tokenUri;
    @Value("${server.zitadel.client-id}")
    private String clientId;
    @Value("${server.zitadel.client-secret}")
    private String clientSecret;
    @Value("${server.zitadel.scope}")
    private String scope;
    @Value("${server.web-client.time-response}")
    private int responseTimeout;
    @Bean
    public ReactiveOAuth2AuthorizedClientManager reactiveAuthorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService) {

        ReactiveOAuth2AuthorizedClientProvider provider =
                ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        var manager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        manager.setAuthorizedClientProvider(provider);
        return manager;
    }

    @Bean
    public ReactiveOAuth2AuthorizedClientService reactiveOAuth2AuthorizedClientService(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrations() {
        ClientRegistration registration = ClientRegistration.withRegistrationId(registrationId)
                .tokenUri(tokenUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope(scope)
                .build();
        return new InMemoryReactiveClientRegistrationRepository(registration);
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }
    @Bean
    public WebClient zitadelWebClient(ReactiveOAuth2AuthorizedClientManager manager) {
        return WebClient.builder()
                .filter((request, next) -> {
                    OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                            .withClientRegistrationId("zitadel")
                            .principal("zitadel-client")
                            .build();

                    return manager.authorize(authorizeRequest)
                            .flatMap(client -> {
                                String token = client.getAccessToken().getTokenValue();
//                                System.out.println("👉 Запрос с токеном: " + token);

                                return Mono.fromCallable(() -> userRepository.findByEmail(oidcEmail))
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .flatMap(userOpt -> {
                                            if (userOpt.isPresent()) {
                                                var date = tokenController.updateToken(userOpt.get(), token);
//                                                System.out.println("🕒 Обновлённый токен: " + token);
//                                                System.out.println("📅 Дата обновления токена: " + date);


                                                ClientRequest authorizedRequest = ClientRequest.from(request)
                                                        .headers(headers -> {
                                                            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                                                            headers.set("X-Token-Date", date);
                                                        })
                                                        .build();

                                                return next.exchange(authorizedRequest);
                                            }

                                            ClientRequest authorizedRequest = ClientRequest.from(request)
                                                    .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                                                    .build();

                                            return next.exchange(authorizedRequest);
                                        });
                            });
                })
//                .filter((request, next) -> {
//                    System.out.println("🚀 Отправка запроса");
//                    return next.exchange(request)
//                            .doOnNext(response ->
//                                    System.out.println("📦 Ответ получен, статус: " + response.statusCode()));
//                })
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(responseTimeout))
                ))
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, this::handleServerErrors)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, this::handleClientErrors)
                .build();
    }


    private Mono<? extends Throwable> handleServerErrors(ClientResponse clientResponse) {
        throw new IllegalStateException(clientResponse.toString());
    }
    private Mono<? extends Throwable> handleClientErrors(ClientResponse clientResponse) {
        throw new IllegalStateException(clientResponse.toString());
    }
}
