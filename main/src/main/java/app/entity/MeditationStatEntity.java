package app.entity;

import app.entity.usermeditation.UserMeditationEntity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "meditation_statistic_data")
@Getter
@Setter
@NoArgsConstructor
public class MeditationStatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private UserMeditationEntity meditationEntity;

    private int pulse;
}
