package app.entity.usermeditation;

import app.entity.MeditationStatEntity;
import app.entity.UserMeditationAlbumEntity;
import app.entity.UserEntity;
import app.entity.MeditationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private UserEntity user;

    private String statuses;

    @Column(name = "pause_time")
    private double pauseTime = 0.0;

    @ManyToMany(
            mappedBy = "meditations",
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH}
    )
    private List<UserMeditationAlbumEntity> albumEntities;

    @OneToMany(
            mappedBy = "meditationEntity",
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<MeditationStatEntity> meditationStatEntities;
}
