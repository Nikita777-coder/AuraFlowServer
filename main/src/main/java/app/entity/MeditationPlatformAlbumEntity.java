package app.entity;

import app.entity.usermeditation.UserMeditationEntity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "meditation_platform_albums")
@Getter
@Setter
@NoArgsConstructor
public class MeditationPlatformAlbumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String title;

    @Column
    private String description;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "meditation_platform_albums_meditation_from_platform",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "meditation_from_platform_id")
    )
    private List<MeditationEntity> meditationsFromPlatform;

    @PreRemove
    private void preRemove() {
        for (MeditationEntity album : meditationsFromPlatform) {
            album.getAlbumEntities().remove(this);
        }
    }

    @ManyToOne
    private UserEntity userEntity;
}
