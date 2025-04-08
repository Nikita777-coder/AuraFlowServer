package app.repository;

import app.dto.meditationalbum.MeditationAlbumPlatform;
import app.entity.MeditationAlbumEntity;
import app.entity.MeditationPlatformAlbumEntity;
import jakarta.persistence.JoinColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MeditationPlatformAlbumRepository extends JpaRepository<MeditationPlatformAlbumEntity, UUID> {
}
