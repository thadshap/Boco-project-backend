package com.example.idatt2106_2022_05_backend.service.email;

import com.example.idatt2106_2022_05_backend.model.Email;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;

@Service
public interface EmailService {
    void sendEmail(Email email) throws MessagingException, IOException;

    void sendEmail(String from, String to, String subject, String message) throws MessagingException;

}
