package app.entity;

import app.dto.meditation.MeditationStatus;
import app.dto.meditation.UploadStatus;
import app.entity.MeditationPlatformAlbumEntity;
import app.entity.usermeditation.UserMeditationEntity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name = "platform_meditations")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class MeditationEntity implements app.entity.Entity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "video_link")
    private String videoLink;

    private UUID taskId;

    @Column(name = "tags")
    private String jsonTags;

    @OneToMany(
            mappedBy = "meditationFromPlatform",
            orphanRemoval = true,
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE}
    )
    private List<UserMeditationEntity> userMeditationEntities;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_seconds")
    private double durationSeconds;

    @Column(name = "creation_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    @Column
    private String author;

    private boolean promoted;

    @ManyToMany(
            mappedBy = "meditationsFromPlatform",
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH}
    )
    private List<MeditationPlatformAlbumEntity> albumEntities;

    @PreRemove
    private void preRemove() {
        for (MeditationPlatformAlbumEntity album : albumEntities) {
            album.getMeditationsFromPlatform().remove(this);
        }
    }

    @Column
    private UploadStatus status;

    private int countStatusRequests = 1;

    private boolean wasUploadedFromUrl;
}
