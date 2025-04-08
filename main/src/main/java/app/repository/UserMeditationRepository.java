package app.repository;

import app.dto.meditation.Status;
import app.entity.usermeditation.UserMeditationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserMeditationRepository extends JpaRepository<UserMeditationEntity, UUID> {
    List<UserMeditationEntity> findAllByUser_EmailAndStatuses(String email, List<Status> statuses);
}
