package app.entity.usermeditation;

import app.entity.UserEntity;
import app.entity.meditation.MeditationEntity;
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

    @OneToOne
    private MeditationEntity meditationFromPlatform;

    @ManyToOne
    private UserEntity user;

    @OneToMany(mappedBy = "statusName")
    private List<StatusEntity> statuses;

    @Column(name = "pause_time")
    private double pauseTime = 0.0;
}
