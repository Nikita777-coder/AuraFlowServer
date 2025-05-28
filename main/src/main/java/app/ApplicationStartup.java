package app;

import app.dto.meditation.UploadStatus;
import app.entity.MeditationEntity;
import app.entity.UserEntity;
import app.entity.userattributes.Role;
import app.repository.MeditationRepository;
import app.repository.UserRepository;
import app.service.MeditationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final MeditationRepository meditationRepository;
    private final MeditationService meditationService;

    @Value("${server.admin.email}")
    private String adminEmail;
    @Value("${server.admin.password}")
    private String adminPassword;
    @Value("${server.admin.name}")
    private String adminName;
    @Value("${server.admin.onesignal-id}")
    private String adminOneSignalId;
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            userRepository.save(UserEntity
                    .builder()
                    .hasPractiseBreathOpt(true)
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

        loadMeditations();
    }

    private void loadMeditations() {
        List<String> allPlatformMeditations = meditationService.getAllPlatformMeditations();
        List<MeditationEntity> allLocalMeditations = meditationRepository.findAll();
        Set<String> meditationLocalVideos = allLocalMeditations.stream().map(MeditationEntity::getVideoLink).collect(Collectors.toSet());
        List<MeditationEntity> videoLinksForAddingToLocalMeditations = new ArrayList<>();

        for (var meditation: allPlatformMeditations) {
            if (!meditationLocalVideos.contains(meditation)) {
                MeditationEntity meditationEntity = new MeditationEntity();
                meditationEntity.setCreatedAt(LocalDateTime.now());
                meditationEntity.setTitle(meditation.split("/")[5].split("\\.")[0]);
                meditationEntity.setVideoLink(meditation);
                meditationEntity.setStatus(UploadStatus.READY);

                videoLinksForAddingToLocalMeditations.add(meditationEntity);
            }
        }

        meditationRepository.saveAll(videoLinksForAddingToLocalMeditations);
    }
}
