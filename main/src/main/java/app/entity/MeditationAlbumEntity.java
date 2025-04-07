package app.entity;

import app.entity.usermeditation.UserMeditationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "meditation_albums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeditationAlbumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String description;

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    private List<UserMeditationEntity> meditations;

    @ManyToOne
    private UserEntity user;
}
