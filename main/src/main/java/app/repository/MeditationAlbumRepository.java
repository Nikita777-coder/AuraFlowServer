package app.repository;

import app.entity.MeditationAlbumEntity;
import app.entity.userattributes.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeditationAlbumRepository extends JpaRepository<MeditationAlbumEntity, UUID> {
    List<MeditationAlbumEntity> findAllByUser_Email(String email);
    List<MeditationAlbumEntity> findAllByUser_Role(Role role);
    Optional<MeditationAlbumEntity> findByTitle(String title);
}
