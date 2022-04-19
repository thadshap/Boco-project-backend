package com.example.idatt2106_2022_05_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.util.Properties;

/**
 * Mail configuration
 */
@Configuration
public class MailConfig {

//    @Value("${spring.mail.host}")
    private String EMAIL_HOST;

//    @Value("${spring.mail.port}")
    private int EMAIL_PORT;

//    @Value("${spring.mail.username}")
    private String EMAIL_USERNAME;

//    @Value("${spring.mail.password}")
    private String EMAIL_PASSWORD;


    /**
     *
     * @return
     * @throws IOException
     */
    @Bean
    public JavaMailSender mailSender() throws IOException  {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(EMAIL_HOST);
        mailSender.setPort(EMAIL_PORT);
        mailSender.setUsername(EMAIL_USERNAME);
        mailSender.setPassword(EMAIL_PASSWORD);

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.starttls.enable","true");
        javaMailProperties.put("mail.smtp.auth","true");
        mailSender.setJavaMailProperties(javaMailProperties);

        return mailSender;
    }
}
