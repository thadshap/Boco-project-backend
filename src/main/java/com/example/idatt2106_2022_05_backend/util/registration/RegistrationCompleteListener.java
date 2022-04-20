package com.example.idatt2106_2022_05_backend.util.registration;

import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.service.authorization.AuthService;
import com.example.idatt2106_2022_05_backend.service.email.EmailService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteListener implements
        ApplicationListener<RegistrationComplete> {

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(RegistrationComplete event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        authService.saveUserVerificationTokenForUser(token,user);

        String url =
                event.getApplicationUrl()
                        + "/verifyEmail?token="
                        + token;
        emailService.sendEmail("BOCO", user.getEmail(), "Konto at BOCO",
                "Kontoen din er nesten klar, " +
                        " klikk på lenken under for å verifisere kontoen din." +
                        "\n" + url);
        log.info("Click the link to verify your account: {}",
                url);
    }
}
