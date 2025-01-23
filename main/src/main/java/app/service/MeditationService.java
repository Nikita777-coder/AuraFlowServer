package app.service;

import app.dto.meditation.MeditationUploadBodyRequest;
import app.entity.userattributes.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeditationService {
    private final WebClientRestService webClientRestService;
//    public UUID uploadMeditation(UserDetails userDetails,
//                            MeditationUploadBodyRequest meditationUploadBodyRequest) {
//        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN))) {
//            throw new AccessDeniedException("access deny");
//        }
//
//        var ans = webClientRestService.post("/integration/kinescoope", meditationUploadBodyRequest);
//        validateAns(ans);
//
//        // дальше надо сохранить видео в бд медитаций и отдать её id
//    }
//    private void validateAns(Object ans) {
//        if (ans.getCode() >= 400 && ans.getCode() < 500) {
//            throw new IllegalArgumentException(ans.getBody());
//        }
//
//        if (ans.getCode() >= 500) {
//            throw new IllegalStateException(ans.getBody());
//        }
//    }
}
