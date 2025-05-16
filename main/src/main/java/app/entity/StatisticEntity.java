package app.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "statistics")
@Getter
@Setter
@NoArgsConstructor
public class StatisticEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private UserEntity user;

    private LocalDate fixedTime;

    private Integer entranceCountPerDay;

    private LocalDate statisticTimeFixing = LocalDate.now();

    @OneToMany(
            mappedBy = "statisticEntity",
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<MeditationStatEntity> watchedMeditationsPerDay;
}
