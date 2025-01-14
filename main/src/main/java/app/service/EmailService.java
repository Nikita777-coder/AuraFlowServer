package app.service;

import app.dto.email.VerificationCodeBody;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Value("${server.email.name}")
    private String serverMail;

    @Value("${server.email.verification-code-length}")
    private int verificationCodeLength;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    public String sendVerificationCode(VerificationCodeBody verificationCodeBody) {
        if (userRepository.findByEmail(verificationCodeBody.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email exists in database");
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(serverMail);
        mailMessage.setTo(verificationCodeBody.getEmail());
        mailMessage.setSubject("AuraFlow, verification code");
        String generatedVerificationPassword = generateSafeVerificationCode();
        mailMessage.setText(String.format("Your verification code - %s", generatedVerificationPassword));

        mailSender.send(mailMessage);

        return generatedVerificationPassword;
    }

    private String generateSafeVerificationCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(verificationCodeLength);

        for (int i = 0; i < verificationCodeLength; i++) {
            int digit = random.nextInt(10);
            code.append(digit);
        }

        return code.toString();
    }
}
