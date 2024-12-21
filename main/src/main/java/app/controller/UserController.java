package app.controller;

import app.dto.user.UpdatePasswordData;
import app.dto.user.UserData;
import app.dto.user.UserOptions;
import app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
// Get current user in user service when call it functions
public class UserController {
    private final UserService userService;

    @GetMapping("/options")
    @ResponseBody
    public UserOptions getOptions() {
        return userService.getOptions();
    }

    @GetMapping
    public UserData getUserData() { return userService.getUserData(); }

    @PatchMapping
    @ResponseBody
    public UserData updateUserData(@Valid @RequestBody UserData userData) {
        return userService.updateUser(userData);
    }

    @PatchMapping("/password")
    @ResponseBody
    public String updatePassword(@RequestBody UpdatePasswordData updatePasswordData) {
        userService.updatePassword(updatePasswordData);
        return "Success";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
