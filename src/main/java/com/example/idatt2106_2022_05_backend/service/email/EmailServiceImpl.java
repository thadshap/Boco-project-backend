package com.example.idatt2106_2022_05_backend.service.email;

import com.example.idatt2106_2022_05_backend.model.Email;
import com.nimbusds.jose.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

/**
 * Implementation of {@link EmailService}
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    /**
     * Method for sending an email with {@link Email} object
     * 
     * @param email
     *            {@link Email} object to send
     * 
     * @throws MessagingException
     *             throws when {@link MimeMessageHelper} throws
     */
    @Override
    public void sendEmail(Email email) throws MessagingException, IOException {
        final MimeMessage message = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                "UTF-8");

        Context context = new Context();
        context.setVariables(email.getTemplate().getVariables());

        helper.setTo(email.getTo());
        helper.setFrom(email.getFrom());
        helper.setSubject(email.getSubject());
        String template = templateEngine.process(email.getTemplate().getTemplate(), context);
        helper.setText(template, true);

        mailSender.send(message);
    }

    /**
     * Method for sending an email
     * 
     * @param from
     *            Sender's address
     * @param to
     *            Reciever's address
     * @param subject
     *            Subject of the email
     * @param message
     *            Actaul email content
     * 
     * @throws MessagingException
     *             throws when {@link MimeMessageHelper} throws
     */
    @Override
    public void sendEmail(String from, String to, String subject, String message) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setText(message);
        helper.setSubject(subject);
        this.mailSender.send(mimeMessage);
    }
}
