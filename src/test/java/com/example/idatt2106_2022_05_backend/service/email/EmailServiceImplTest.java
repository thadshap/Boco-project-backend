package com.example.idatt2106_2022_05_backend.service.email;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.idatt2106_2022_05_backend.config.MailConfig;
import com.example.idatt2106_2022_05_backend.model.Email;
import com.example.idatt2106_2022_05_backend.model.ThymeleafTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {


    @SpyBean
    private JavaMailSender mailSender;

    @Autowired
    @InjectMocks
    private EmailServiceImpl emailService;

    private Email mail;


    @BeforeEach
    void setUp() {
        String message =
        "name"+ "Test Testesen" +
        "Testing" + "Min test" +
        "url" + "https://bocotest.web.com/";

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "Test Testesen");
        variables.put("url", "https://bocotest.web.com/");

        mail = Email.builder()
                .from("hassano19988991@gmail.com")
                .to("andetel@stud.ntnu.no")
                .template(new ThymeleafTemplate("verify_mail", variables))
                .subject("testing")
                .build();

        Mockito.doNothing().when(mailSender).send(any(MimeMessage.class));

    }

    @Test
    void sendEmail() throws MessagingException {
        emailService.sendEmail("hassano19988991@gmail.com","ken@robin.no", "Activity closed","name"+ "Test Testesen" +
                "Testing" + "Min test" +
                "url" + "https://bocotest.web.com/");
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendEmail() throws MessagingException, IOException {
        emailService.sendEmail(mail);
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}