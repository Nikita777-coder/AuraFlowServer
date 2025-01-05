package app.entity;

import app.entity.userattributes.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(
        name="users",
        indexes = @Index(columnList = "email")
)
@Getter
@Setter
@NoArgsConstructor
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

    @Column
    private LocalTime startTimeOfBreathPractise = LocalTime.of(8, 0);

    @Column
    private LocalTime stopTimeOfBreathPractise = LocalTime.of(22, 0);

    @Column
    private Integer countBreathPractiseReminderPerDay = 4;

    @Column
    private Boolean isPremium;

    @Column(columnDefinition = "varchar(32) default 'USER'")
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

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

