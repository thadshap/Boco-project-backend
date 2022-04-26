package com.example.idatt2106_2022_05_backend.service.authorization;

import com.example.idatt2106_2022_05_backend.dto.user.CreateAccountDto;
import com.example.idatt2106_2022_05_backend.dto.user.LoginDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserForgotPasswordDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserRenewPasswordDto;
import com.example.idatt2106_2022_05_backend.model.ResetPasswordToken;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.model.UserVerificationToken;
import com.example.idatt2106_2022_05_backend.repository.ResetPasswordTokenRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.repository.UserVerificationTokenRepository;
import com.example.idatt2106_2022_05_backend.security.JWTUtil;
import com.example.idatt2106_2022_05_backend.service.email.EmailService;
import com.example.idatt2106_2022_05_backend.service.user.UserDetailsServiceImpl;
import com.example.idatt2106_2022_05_backend.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
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
    private JWTUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserVerificationTokenRepository userVerificationTokenRepository;

    @Autowired
    ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public User createUser(CreateAccountDto createAccount) {
        if (userRepository.findByEmail(createAccount.getEmail()) != null) {
            return null;
        }

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
        UserVerificationToken verificationToken = userVerificationTokenRepository.findByToken(token).get();

        if (verificationToken == null) {
            return "Invalid email";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            userVerificationTokenRepository.delete(verificationToken);
            return "Valideringstid utløpt";
        }

        user.setVerified(true);
        userRepository.save(user);
        return "valid email";
    }

    @Override
    public Response createNewToken(String prevToken, HttpServletRequest url) throws MessagingException {
        UserVerificationToken verificationToken = userVerificationTokenRepository.findByToken(prevToken).get();

        // if (verificationToken == null) {
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found in repository");
        // } TODO exception

        verificationToken.setToken(UUID.randomUUID().toString());
        userVerificationTokenRepository.save(verificationToken);
        User user = verificationToken.getUser();

        String newUrl = "http://" + url.getServerName() + ":" + url.getServerPort() + url.getContextPath()
                + "/auth/verifyRegistration?token=" + verificationToken.getToken();

        emailService.sendEmail("BOCO", user.getEmail(), "Konto i BOCO",
                "Kontoen din er nesten klar, " + " klikk på lenken under for å verifisere kontoen din." + "\n" + url);
        // TODO create own mail

        log.info("Click the link to verify your account: {}", newUrl);
        return new Response("Verifikasjons mail er sendt til din email!", HttpStatus.ACCEPTED);
    }

    @Override
    public Response resetPassword(UserForgotPasswordDto forgotPasswordDto, String url) throws MessagingException {
        User user = userRepository.findByEmail(forgotPasswordDto.getEmail());

        if (user != null) {
            String token = UUID.randomUUID().toString();
            ResetPasswordToken resetToken = new ResetPasswordToken(user, token);
            resetPasswordTokenRepository.save(resetToken);
            emailService.sendEmail("BOCO", user.getEmail(), "Konto i BOCO, nytt passord",
                    "Klikk på lenken under for å endre passordet ditt." + "\n" + url + "/auth/renewYourPassword?token="
                            + token);//TODO renewYourPassword skal sende bruker til form som skal sende til /renewPassword
            log.info("Click the link to change your account: {}", url + "/auth/renewYourPassword?token="
                    + token);
            return new Response("Reset passord link sent til mail.", HttpStatus.ACCEPTED);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bruker med forgotPasswordDto er ikke funnet!");
    }

    @Override
    public Response validatePasswordThroughToken(String token, UserRenewPasswordDto forgotPasswordDto) {
        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByToken(token);

        if (resetPasswordToken == null) {
            return new Response("Passord to ken ikke funnet", HttpStatus.NOT_FOUND);
        }

        User user = resetPasswordToken.getUser();
        Calendar cal = Calendar.getInstance();

        if ((resetPasswordToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            resetPasswordTokenRepository.delete(resetPasswordToken);
            return new Response("Passord to ken tid utgått", HttpStatus.GONE);
        }

        user.setPassword(passwordEncoder.encode(forgotPasswordDto.getPassword()));
        userRepository.save(user);

        return new Response("valid token", HttpStatus.CREATED);
    }

    @Override
    public Response login(LoginDto loginDto) throws Exception {

        System.out.println(loginDto.getEmail() + " " + loginDto.getPassword());

        if (userRepository.findByEmail(loginDto.getEmail()) == null) {
            // throw exception
            log.info("Did not find user with email {} ", loginDto.getEmail());
            return new Response("Email er feil", HttpStatus.NOT_FOUND);
        }
        if (!passwordEncoder.matches(loginDto.getPassword(),
                userRepository.findByEmail(loginDto.getEmail()).getPassword())) {
            log.info("[X] Password check failed for user with email {}", loginDto.getEmail());
            return new Response("Passord er feil", HttpStatus.NOT_FOUND);
        }
        final UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(loginDto.getEmail());

        final String token = jwtUtil.generateToken(userDetails);

        return new Response(token, HttpStatus.ACCEPTED);
    }

    @Override
    public void updateAuthenticationType(String username, String oauth2ClientName) {
        // userRepository.
        // TODO verify user logging in by facebook and google and put them in repo
    }

    @Override
    public String getUserJWT(String token) {
        UserVerificationToken verificationToken = userVerificationTokenRepository.findByToken(token).get();
        final UserDetails userDetails = userDetailsServiceImpl
                .loadUserByUsername(verificationToken.getUser().getEmail());

        return jwtUtil.generateToken(userDetails);
    }
}
