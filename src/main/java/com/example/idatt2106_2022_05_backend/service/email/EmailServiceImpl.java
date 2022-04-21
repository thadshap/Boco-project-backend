package com.example.idatt2106_2022_05_backend.service.email;

import com.example.idatt2106_2022_05_backend.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Implementation of {@link EmailService}
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

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
    public void sendEmail(Email email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                "UTF-8");

        helper.setTo(email.getTo());
        helper.setFrom(email.getFrom());
        helper.setSubject(email.getSubject());
        helper.setText(email.getMessage());

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
