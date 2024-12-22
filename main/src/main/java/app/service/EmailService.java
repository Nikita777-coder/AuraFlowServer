package app.service;

import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Value("${server.email.name}")
    private String serverMail;

    @Value("${server.email.verification-code-length}")
    private int verificationCodeLength;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    public String sendVerificationCode(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email exists in database");
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(serverMail);
        mailMessage.setTo(email);
        mailMessage.setSubject("AuraFlow, verification code");
        String generatedVerificationPassword = generateSafeVerificationCode();
        mailMessage.setText(String.format("Your verification code - %s", generatedVerificationPassword));

        mailSender.send(mailMessage);

        return generatedVerificationPassword;
    }

    private String generateSafeVerificationCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[verificationCodeLength];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes).substring(0, verificationCodeLength);
    }
}
