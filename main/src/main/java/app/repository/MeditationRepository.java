package app.repository;

import app.dto.meditation.MeditationStatus;
import app.entity.meditation.MeditationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MeditationRepository extends JpaRepository<MeditationEntity, UUID> {
    @Query("SELECT me FROM MeditationEntity me WHERE CURRENT_DATE - 14 <= me.createdAt and me.status = 'DONE'")
    List<MeditationEntity> findNewMeditations();
    List<MeditationEntity> findAllByStatus(MeditationStatus status);
}
