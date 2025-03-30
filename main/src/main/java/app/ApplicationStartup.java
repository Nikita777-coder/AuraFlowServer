package app;

import app.entity.UserEntity;
import app.entity.userattributes.Role;
import app.repository.MeditationRepository;
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
    private final MeditationRepository meditationRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
//        var entity = meditationRepository.findByTitle("Медитация перед сном для успокоения нервов | Медитация на ночь от стресса");
//        entity.setVideoLink("https://storage.yandexcloud.net/auraflow/sleepMeditation.mp4");
//        entity.setAuthor("service");
//        meditationRepository.save(entity);
//
//        entity = meditationRepository.findByTitle("Медитация для успокоения нервов и продуктивности | Дневная медитация на упех | Медитация на работе");
//        entity.setVideoLink("https://storage.yandexcloud.net/auraflow/workMeditation.mp4");
//        entity.setAuthor("service");
//        meditationRepository.save(entity);
//
//        entity = meditationRepository.findByTitle("МЕДИТАЦИЯ ДЛЯ СПОКОЙСТВИЯ и эмоционального восстановления");
//        entity.setVideoLink("https://storage.yandexcloud.net/auraflow/relaxMeditation.mp4");
//        entity.setAuthor("service");
//        meditationRepository.save(entity);
//
//        entity = meditationRepository.findByTitle("Утренняя медитация 6 минут: женская энергия");
//        entity.setVideoLink("https://storage.yandexcloud.net/auraflow/morningMeditation.mp4");
//        entity.setAuthor("service");
//        meditationRepository.save(entity);
//
//        entity = meditationRepository.findByTitle("ШАВАСАНА. ГЛУБОКОЕ РАССЛАБЛЕНИЕ. МЕДИТАЦИЯ. 5 МИНУТ.");
//        entity.setVideoLink("https://storage.yandexcloud.net/auraflow/shavanaMeditation.mp4");
//        entity.setAuthor("service");
//        meditationRepository.save(entity);
        userRepository.save(UserEntity
                .builder()
                .countBreathPractiseReminderPerDay(4)
                .email("admin@a.com")
                .isPremium(true)
                .name("name")
                .role(Role.ADMIN)
                .password(bCryptPasswordEncoder.encode("aDm12q!sdkIs"))
                .build());
    }
}
