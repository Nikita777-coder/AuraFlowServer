package app.entity.usermeditation;

import app.entity.MeditationAlbumEntity;
import app.entity.MeditationPlatformAlbumEntity;
import app.entity.UserEntity;
import app.entity.meditation.MeditationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_meditations")
@Getter
@Setter
@NoArgsConstructor
public class UserMeditationEntity implements app.entity.Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String title;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    private MeditationEntity meditationFromPlatform;

    @Column(name = "generated_meditation_link")
    private String generatedMeditationLink;

    @ManyToOne
    private UserEntity user;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<StatusEntity> statuses;

    @Column(name = "pause_time")
    private double pauseTime = 0.0;

    @ManyToMany(
            mappedBy = "meditations",
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH}
    )
    private List<MeditationAlbumEntity> albumEntities;
}
