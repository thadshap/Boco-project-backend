package com.example.idatt2106_2022_05_backend.service.authorization;

import com.example.idatt2106_2022_05_backend.dto.CreateAccountDto;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.model.UserVerificationToken;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.repository.UserVerificationTokenRepository;
import com.example.idatt2106_2022_05_backend.service.authorization.AuthService;
import com.example.idatt2106_2022_05_backend.service.email.EmailService;
import com.example.idatt2106_2022_05_backend.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserVerificationTokenRepository userVerificationTokenRepository;

    @Autowired
    private EmailService emailService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public User createUser(CreateAccountDto createAccount) {
        User user = modelMapper.map(createAccount, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public void saveUserVerificationTokenForUser(String token, User user) {
        UserVerificationToken userVerificationToken = new UserVerificationToken(user, token);

        userVerificationTokenRepository.save(userVerificationToken);
    }

    @Override
    public String validateEmailThroughToken(String token) {
        UserVerificationToken verificationToken
                = userVerificationTokenRepository.findByToken(token).get();

        if (verificationToken == null) {
            return "Invalid email";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <= 0) {
            userVerificationTokenRepository.delete(verificationToken);
            return "Valideringstid utløpt";
        }

        user.setVerified(true);
        userRepository.save(user);
        return "valid email";
    }

    @Override
    public Response createNewToken(String prevToken, HttpServletRequest url) throws MessagingException {
        UserVerificationToken verificationToken
                = userVerificationTokenRepository.findByToken(prevToken).get();
//        if (verificationToken == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found in repository");
//        } TODO exception
        verificationToken.setToken(UUID.randomUUID().toString());
        userVerificationTokenRepository.save(verificationToken);
        User user = verificationToken.getUser();
        String newUrl = "http://" +
                url.getServerName() +
                ":" +
                url.getServerPort() +
                url.getContextPath() +
                "/verifyRegistration?token="
                + verificationToken.getToken();
        emailService.sendEmail("BOCO", user.getEmail(), "Konto at BOCO",
                "Kontoen din er nesten klar, " +
                        " klikk på lenken under for å verifisere kontoen din." +
                        "\n" + url);
        //TODO create own mail
        log.info("Click the link to verify your account: {}",
                newUrl);
        return new Response("Verifikasjons mail er sendt til din email!", HttpStatus.ACCEPTED);
    }


}
