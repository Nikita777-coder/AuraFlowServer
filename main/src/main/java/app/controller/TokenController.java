package app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {
    @Value("${server.oidc.email}")
    private String oidcEmail;
    private final Map<String, String> tokens = new ConcurrentHashMap<>();

    @GetMapping
    public String getToken(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String time) {
        checkAccess(userDetails);
        var token = tokens.get(time);
        tokens.remove(time);

        return token;
    }
    @PatchMapping
    public String updateToken(@AuthenticationPrincipal UserDetails userDetails, @RequestBody String token) {
        checkAccess(userDetails);
        var date = LocalDateTime.now().toString();
        tokens.put(date, token);

        return date;
    }
    private void checkAccess(UserDetails userDetails) {
        if (!userDetails.getUsername().equals(oidcEmail)) {
            throw new AccessDeniedException("access deny");
        }
    }
}
