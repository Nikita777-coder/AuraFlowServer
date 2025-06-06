package app.entity;

import app.entity.payment.PremiumEntity;
import app.entity.userattributes.Role;
import app.entity.usermeditation.UserMeditationEntity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;
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
@ToString
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String password;

    @Column(unique = true, length = 500)
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
    private int countBreathPractiseReminderPerDay = 4;

    @Column
    private Boolean isPremium;

    private Boolean isBlocked;

    private Boolean isExitButtonPressed;

    @OneToMany(orphanRemoval = true,
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE},
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    private List<UserMeditationEntity> userMeditations;

    @OneToMany(orphanRemoval = true,
            cascade = {CascadeType.REMOVE, CascadeType.REFRESH},
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    private List<UserMeditationAlbumEntity> meditationAlbumEntities;

    @OneToMany(
            orphanRemoval = true,
            mappedBy = "userEntity",
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE},
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    private List<PremiumEntity> premiumEntities;

    @OneToMany(
            mappedBy = "userEntity"
    )
    @ToString.Exclude
    private List<MeditationPlatformAlbumEntity> meditationPlatformAlbumEntities;

    @PreRemove
    private void preRemove() {
        for (MeditationPlatformAlbumEntity album : meditationPlatformAlbumEntities) {
            album.setUserEntity(null);
        }
    }

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

