package app.entity.meditation;

import app.dto.meditation.MeditationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name = "meditations")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class MeditationEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "video_link")
    private String videoLink;

    private UUID videoId;

    @OneToMany
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

    @Column
    private MeditationStatus status;

    private boolean wasUploadedFromUrl;
}
