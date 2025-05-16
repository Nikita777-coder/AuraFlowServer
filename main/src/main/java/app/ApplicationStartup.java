package app;

import app.entity.UserEntity;
import app.entity.userattributes.Role;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Value("${server.admin.email}")
    private String adminEmail;
    @Value("${server.admin.password}")
    private String adminPassword;
    @Value("${server.admin.name}")
    private String adminName;
    @Value("${server.admin.onesignal-id}")
    private String adminOneSignalId;
    @Value("${server.oidc.email}")
    private String oidcEmail;
    @Value("${server.oidc.password}")
    private String oidcPassword;
    @Value("${server.admin.onesignal-id}")
    private String oidcOneSignalId;
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            userRepository.save(UserEntity
                    .builder()
                    .hasPractiseBreathOpt(true)
                    .isBlocked(false)
                    .isExitButtonPressed(false)
                    .oneSignalId(UUID.fromString(adminOneSignalId))
                    .startTimeOfBreathPractise(LocalTime.of(1, 0))
                    .stopTimeOfBreathPractise(LocalTime.of(23, 59))
                    .countBreathPractiseReminderPerDay(4)
                    .email(adminEmail)
                    .isPremium(true)
                    .name(adminName)
                    .role(Role.ADMIN)
                    .password(bCryptPasswordEncoder.encode(adminPassword))
                    .build());
        }

        if (userRepository.findByEmail(oidcEmail).isEmpty()) {
            userRepository.save(UserEntity
                    .builder()
                    .hasPractiseBreathOpt(true)
                    .isBlocked(false)
                    .isExitButtonPressed(false)
                    .oneSignalId(UUID.fromString(oidcOneSignalId))
                    .startTimeOfBreathPractise(LocalTime.of(1, 0))
                    .stopTimeOfBreathPractise(LocalTime.of(23, 59))
                    .countBreathPractiseReminderPerDay(4)
                    .email(oidcEmail)
                    .isPremium(true)
                    .role(Role.USER)
                    .password(bCryptPasswordEncoder.encode(oidcPassword))
                    .build());
        }
    }
}
