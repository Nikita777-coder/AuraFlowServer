package app.service;

import app.dto.meditation.GeneratedMeditation;
import app.dto.meditation.ModelMeditationRequest;
import app.repository.UserRepository;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeditationAiService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final WebClientRestService webClientRestService;

    @Value("${server.integration.base-url}")
    private String integrationBaseUrl;
    @Value("${server.integration.meditation-ai-path}")
    private String integrationGeneratePath;
    public Mono<String> generatedMeditation(UserDetails userDetails,
                                            ModelMeditationRequest modelMeditationRequest) {
        return Mono.fromCallable(() -> userService.getUserByEmail(userDetails.getUsername()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(user -> {
                    if (!user.getIsPremium() && user.getCountOfGenerations() == 3) {
                        return Mono.error(new IllegalArgumentException("ваш лимит на генерацию закончился!"));
                    }

                    user.setCountOfGenerations(user.getCountOfGenerations() + 1);
                    return Mono.fromCallable(() -> userRepository.save(user))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then(webClientRestService.post(
                                    integrationBaseUrl,
                                    integrationGeneratePath,
                                    modelMeditationRequest,
                                    String.class
                            ).onErrorResume(ReadTimeoutException.class, ex ->
                                    Mono.fromRunnable(() -> {
                                                user.setCountOfGenerations(user.getCountOfGenerations() - 1);
                                                userRepository.save(user);
                                            })
                                            .subscribeOn(Schedulers.boundedElastic())
                                            .then(Mono.error(ex))
                            ));
                });
    }
    public Mono<GeneratedMeditation> getMeditation(String id) {
        return webClientRestService.get(
              integrationBaseUrl,
              integrationGeneratePath,
              Map.of("id", id),
              GeneratedMeditation.class
        );
    }
}
