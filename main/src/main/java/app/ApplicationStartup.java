package app;

import app.entity.UserEntity;
import app.entity.userattributes.Role;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        userRepository.save(UserEntity
                .builder()
                        .countBreathPractiseReminderPerDay(4)
                        .email("admin@a.com")
                        .isPremium(true)
                        .name("name")
                        .role(Role.ADMIN)
                        .password(bCryptPasswordEncoder.encode("aDm12-q!sdkIs"))
                .build());
    }
}
