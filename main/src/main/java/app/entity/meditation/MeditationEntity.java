package app.entity.meditation;

import app.dto.meditation.MeditationStatus;
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

    private UUID videoId;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<TagEntity> tags;

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

    @Column
    private MeditationStatus status;

    private boolean wasUploadedFromUrl;
}
