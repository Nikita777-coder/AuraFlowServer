package app.entity;

import app.entity.usermeditation.UserMeditationEntity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
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
public class UserMeditationAlbumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String title;

    @Column
    private String description;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "meditation_albums_meditation_from_user",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "meditation_from_user_id")
    )
    private List<UserMeditationEntity> meditations;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private UserEntity user;

    @PreRemove
    private void preRemove() {
        for (UserMeditationEntity album : meditations) {
            album.getAlbumEntities().remove(this);
        }
    }
}
