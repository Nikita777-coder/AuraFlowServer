package app.extra;

import app.entity.Entity;
import app.entity.userattributes.Role;
import lombok.Getter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Getter
public class ProgramCommons {
    public void checkUserRole(UserDetails userDetails) {
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN))) {
            throw new AccessDeniedException("access deny");
        }
    }
    public boolean isUserAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN));
    }
    public <T, R extends JpaRepository<T, UUID>> T getAlbumById(UUID id, R repository) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("No such album"));
    }
    public <T extends Entity, R extends JpaRepository<T, UUID>> List<T> getAlbumMeditationsByIds(List<UUID> ids, R repository) {
        List<T> meditationEntities = new ArrayList<>(ids.size());

        for (UUID id : ids) {
            meditationEntities.add(repository.findById(id).orElseThrow(() -> new IllegalArgumentException("no such meditation")));
        }

        return meditationEntities;
    }
}
