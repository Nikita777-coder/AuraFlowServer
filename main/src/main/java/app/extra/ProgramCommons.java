package app.extra;

import app.entity.userattributes.Role;
import app.extra.storageparams.KinescopeSrorageParams;
import app.extra.storageparams.StorageParams;
import app.extra.storageparams.YandexcloudStorageParams;
import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class ProgramCommons {
    private final Map<String, StorageParams> params = new HashMap<>() {{
        put("kinescope", new KinescopeSrorageParams());
        put("yandexcloud", new YandexcloudStorageParams());
    }};

    public void checkUserRole(UserDetails userDetails) {
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN))) {
            throw new AccessDeniedException("access deny");
        }
    }
    public boolean isUserAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN));
    }
}
