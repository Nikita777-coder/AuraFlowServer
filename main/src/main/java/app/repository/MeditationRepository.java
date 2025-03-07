package app.repository;

import app.dto.meditation.MeditationStatus;
import app.entity.meditation.MeditationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MeditationRepository extends JpaRepository<MeditationEntity, UUID> {
    List<MeditationEntity> findAllByCreatedAtAfterAndStatus(LocalDateTime createdAt, MeditationStatus status);
    List<MeditationEntity> findAllByStatusIn(List<MeditationStatus> meditationStatuses);
    List<MeditationEntity> findAllByStatus(MeditationStatus meditationStatus);

}
