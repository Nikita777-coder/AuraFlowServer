package app.entity;

import app.entity.userattributes.Role;
import app.entity.usermeditation.UserMeditationEntity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.Check;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name="users",
        indexes = @Index(columnList = "email")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Check(constraints = "generation_meditation_count <= 3")
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String password;

    @Column(unique = true)
    private String email;

    @Column
    private String name;

    @Column(name = "isPractiseBreathOptionTurned")
    private Boolean hasPractiseBreathOpt = false;

    @Column(name = "isOpenApplicationOptionTurned")
    private Boolean hasOpenAppOpt = false;

    private UUID oneSignalId;

    @Column
    private LocalTime startTimeOfBreathPractise = LocalTime.of(8, 0);

    @Column
    private LocalTime stopTimeOfBreathPractise = LocalTime.of(22, 0);

    @Column
    private Integer countBreathPractiseReminderPerDay = 4;

    @Column
    private Boolean isPremium;

    @OneToMany(orphanRemoval = true,
            cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
    private List<UserMeditationEntity> userMeditations;

    @OneToMany(orphanRemoval = true,
            cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
    private List<MeditationAlbumEntity> meditationAlbumEntities;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

    @Column(
            name = "generation_meditation_count"
    )
    private int countOfGenerations;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}

