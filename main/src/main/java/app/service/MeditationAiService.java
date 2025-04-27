package app.service;

import app.dto.meditation.GeneratedMeditation;
import app.dto.meditation.ModelMeditationRequest;
import app.repository.UserRepository;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
    public String generatedMeditation(UserDetails userDetails,
                                    ModelMeditationRequest modelMeditationRequest) {
        var user = userService.getUserByEmail(userDetails.getUsername());
        user.setCountOfGenerations(user.getCountOfGenerations() + 1);
        userRepository.save(user);
        String generatedMeditation;

        try {
            generatedMeditation = webClientRestService.post(
                    integrationBaseUrl,
                    integrationGeneratePath,
                    modelMeditationRequest,
                    String.class
            );
        } catch (ReadTimeoutException ex) {
            user.setCountOfGenerations(user.getCountOfGenerations() - 1);
            userRepository.save(user);
            throw ex;
        }

        return generatedMeditation;
    }
    public GeneratedMeditation getMeditation(String id) {
        return webClientRestService.get(
              integrationBaseUrl,
              integrationGeneratePath,
              Map.of("id", id),
              GeneratedMeditation.class
        );
    }
}
