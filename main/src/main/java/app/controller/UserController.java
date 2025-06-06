package app.controller;

import app.dto.user.UpdatePasswordData;
import app.dto.user.UserData;
import app.dto.user.UserOptions;
import app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/options")
    @ResponseBody
    public UserOptions getOptions(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getOptions(userDetails);
    }

    @GetMapping
    public UserData getUserData(@AuthenticationPrincipal UserDetails userDetails) { return userService.getUserData(userDetails); }

    @PatchMapping
    @ResponseBody
    public UserData updateUserData(@AuthenticationPrincipal UserDetails userDetails,
                                   @Valid @RequestBody UserData userData) {
        return userService.updateUser(userDetails, userData);
    }

    @PatchMapping("/password")
    @ResponseBody
    public String updatePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestBody UpdatePasswordData updatePasswordData) {
        userService.updatePassword(userDetails, updatePasswordData);
        return "Success";
    }

    @PatchMapping("/block")
    public String blockUser(@AuthenticationPrincipal UserDetails userDetails,
                          @RequestParam String email) {
        userService.blockUser(userDetails, email);
        return "Success";
    }
    @PatchMapping("/unlock")
    public String unlockUser(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam String email) {
        userService.unlockUser(userDetails, email);
        return "Success";
    }
    @DeleteMapping
    public String delete(@AuthenticationPrincipal UserDetails userDetails) {
        userService.delete(userDetails);
        return "Success";
    }
}
