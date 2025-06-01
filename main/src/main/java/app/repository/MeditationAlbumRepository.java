package app.repository;

import app.entity.UserMeditationAlbumEntity;
import app.entity.userattributes.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeditationAlbumRepository extends JpaRepository<UserMeditationAlbumEntity, UUID> {
    List<UserMeditationAlbumEntity> findAllByUser_Email(String email);
    List<UserMeditationAlbumEntity> findAllByUser_Role(Role role);
    Optional<UserMeditationAlbumEntity> findByTitle(String title);
}
