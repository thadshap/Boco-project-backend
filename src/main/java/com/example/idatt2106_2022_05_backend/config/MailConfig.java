package com.example.idatt2106_2022_05_backend.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.util.Properties;

/**
 * Mail configuration
 */
@Configuration
@PropertySource("classpath:/application.yml")
public class MailConfig {

    @Value("${spring.mail.host}")
    private String EMAIL_HOST;

    @Value("${spring.mail.port}")
    private int EMAIL_PORT;

    @Value("${spring.mail.username}")
    private String EMAIL_USERNAME;

    @Value("${spring.mail.password}")
    private String EMAIL_PASSWORD;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Bean to configure sending of mail
     * 
     * @return {@link JavaMailSender} object
     */
    @Bean
    public JavaMailSender mailSender() throws IOException {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(EMAIL_HOST);
        mailSender.setPort(EMAIL_PORT);
        mailSender.setUsername(EMAIL_USERNAME);
        mailSender.setPassword(EMAIL_PASSWORD);

        final Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.starttls.enable", "true");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.smtp.starttls.required", "true");
        javaMailProperties.put("mail.default-encoding", "UTF-8");
        javaMailProperties.load(this.applicationContext.getResource("classpath:application.yml").getInputStream());
        mailSender.setJavaMailProperties(javaMailProperties);

        return mailSender;
    }
}
