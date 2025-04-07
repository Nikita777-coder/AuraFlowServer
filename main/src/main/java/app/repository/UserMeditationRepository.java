package app.repository;

import app.entity.usermeditation.UserMeditationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserMeditationRepository extends JpaRepository<UserMeditationEntity, UUID> {
}
