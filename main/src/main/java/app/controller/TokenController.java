package app.controller;

import app.extra.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {
    @Value("${server.oidc.email}")
    private String oidcEmail;
    private final Map<String, Pair<String, AtomicInteger>> tokens = new ConcurrentHashMap<>();

    @GetMapping
    public String getToken(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String time) {
        checkAccess(userDetails);
        var data = tokens.get(time);

        data.getSecond().addAndGet(1);
        var token = data.getFirst();
        if (data.getSecond().intValue() > 1) {
            tokens.remove(time);
        }

        System.out.printf("%s:%s\n", time, token);

        return token;
    }
    @PatchMapping
    public String updateToken(@AuthenticationPrincipal UserDetails userDetails, @RequestBody String token) {
        checkAccess(userDetails);
        var date = LocalDateTime.now().toString();
        Pair<String, AtomicInteger> pair = new Pair<>(token, new AtomicInteger(0));

        tokens.put(date, pair);

        return date;
    }
    private void checkAccess(UserDetails userDetails) {
        if (!userDetails.getUsername().equals(oidcEmail)) {
            throw new AccessDeniedException("access deny");
        }
    }
}
