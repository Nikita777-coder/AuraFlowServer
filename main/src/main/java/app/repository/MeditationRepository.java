package app.repository;

import app.dto.meditation.MeditationStatus;
import app.entity.MeditationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MeditationRepository extends JpaRepository<MeditationEntity, UUID> {
    List<MeditationEntity> findAllByCreatedAtAfterAndStatus(LocalDateTime createdAt, MeditationStatus status);
    List<MeditationEntity> findAllByStatusIn(List<MeditationStatus> meditationStatuses);
    MeditationEntity findByTitle(String title);
    List<MeditationEntity> findAllByPromoted(boolean promoted);
}
