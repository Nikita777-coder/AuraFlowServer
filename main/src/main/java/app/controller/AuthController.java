package app.controller;

import app.dto.SignInRequest;
import app.dto.SignUpRequest;
import app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseBody
    public String createUser(@Valid @RequestBody SignUpRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/signin")
    @ResponseBody
    public String enter(@Valid @RequestBody SignInRequest request) {
        return authService.signin(request);
    }
}
