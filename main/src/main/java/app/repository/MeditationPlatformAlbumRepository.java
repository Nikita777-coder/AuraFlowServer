package app.repository;

import app.entity.MeditationPlatformAlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MeditationPlatformAlbumRepository extends JpaRepository<MeditationPlatformAlbumEntity, UUID> {
}
