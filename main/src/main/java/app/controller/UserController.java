package app.controller;

import app.dto.UserOptions;
import app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/options")
    public UserOptions getOptions(@RequestParam("id") UUID userId) {
        return userService.getOptions(userId);
    }
}
