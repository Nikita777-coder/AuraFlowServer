package app.repository;

import app.dto.meditation.MeditationStatus;
import app.dto.meditation.UploadStatus;
import app.entity.MeditationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MeditationRepository extends JpaRepository<MeditationEntity, UUID> {
    List<MeditationEntity> findAllByCreatedAtAfterAndStatus(LocalDateTime createdAt, UploadStatus status);
    List<MeditationEntity> findAllByStatusIn(List<UploadStatus> meditationStatuses);
    MeditationEntity findByTitle(String title);
    List<MeditationEntity> findAllByPromoted(boolean promoted);
}
