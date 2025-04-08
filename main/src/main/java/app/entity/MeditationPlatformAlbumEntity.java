package app.entity;

import app.entity.meditation.MeditationEntity;
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

    @OneToMany(cascade = CascadeType.REFRESH)
    private List<MeditationEntity> meditationFromPlatform;

    @ManyToOne
    private UserEntity userEntity;
}
