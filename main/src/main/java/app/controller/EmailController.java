package app.controller;

import app.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send-code")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ResponseBody
    public String sendVerificationCode(@RequestParam("email") String email) {
        return emailService.sendVerificationCode(email);
    }
}
