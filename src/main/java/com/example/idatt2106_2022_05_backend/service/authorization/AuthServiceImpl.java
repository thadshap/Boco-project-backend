package com.example.idatt2106_2022_05_backend.service.authorization;

import com.example.idatt2106_2022_05_backend.dto.user.LoginResponse;
import com.example.idatt2106_2022_05_backend.dto.user.*;
import com.example.idatt2106_2022_05_backend.enums.AuthenticationType;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.model.facebook.FacebookUser;
import com.example.idatt2106_2022_05_backend.repository.PictureRepository;
import com.example.idatt2106_2022_05_backend.repository.ResetPasswordTokenRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.repository.UserVerificationTokenRepository;
import com.example.idatt2106_2022_05_backend.security.JWTUtil;
import com.example.idatt2106_2022_05_backend.security.SecurityService;
import com.example.idatt2106_2022_05_backend.service.email.EmailService;
import com.example.idatt2106_2022_05_backend.service.user.UserDetailsServiceImpl;
import com.example.idatt2106_2022_05_backend.util.Response;
//import com.example.idatt2106_2022_05_backend.util.registration.RegistrationComplete;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.*;

//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

/**
 * Service class to handle authorization of different user requests
 */
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

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private FacebookClient facebookClient;

    @Autowired
    private PictureRepository pictureRepository;

    /**
     * Method to get user from facebook and log them in to.
     *
     * @param accessToken
     *            access code to get user information from facebook.
     *
     * @return user login dto.
     */
    @Override
    public Response loginUserFacebook(String accessToken) throws IOException {
        FacebookUser facebookUser = facebookClient.getUser(accessToken);

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(facebookUser.getEmail());

        if (userDetails == null) {
            User user = User.builder().email(facebookUser.getEmail()).firstName(facebookUser.getFirstName())
                    .lastName(facebookUser.getLastName()).verified(true)
                    .password(passwordEncoder.encode(generatePassword(8))).build();
            Picture picture = profilePicture(user);
            userRepository.save(user);
            pictureRepository.save(picture);

            userDetails = userDetailsServiceImpl.loadUserByUsername(facebookUser.getEmail());
            log.debug(userDetails.getUsername() + " created");
        }
        User user2 = userRepository.findByEmail(facebookUser.getEmail());
        final String token = jwtUtil.generateToken(userDetails);
        System.out.println(userDetails.getUsername());
        System.out.println(token);
        System.out.println(user2.getEmail());
        LoginResponse jwt = LoginResponse.builder().id(user2.getId()).token(token).build();
        return new Response(jwt, HttpStatus.ACCEPTED);
    }

    /**
     *
     * Method to get a users profile picture
     *
     * @param user user to find profile picture of
     * @return returns profile picture
     * @throws IOException
     */
    private Picture profilePicture(User user) throws IOException {
        File pb = new File("src/main/resources/static/images/random/austin-chan-ukzHlkoz1IE-unsplash.jpg");
        byte[] fileContent = Files.readAllBytes(pb.toPath());
        Picture picture1 = Picture.builder().filename(pb.getName())
                .base64(Base64.getEncoder().encodeToString(fileContent)).type(Files.probeContentType(pb.toPath()))
                .build();
        user.setPicture(picture1);
        picture1.setUser(user);
        System.out.println("kj'm hit");
        System.out.println("kj'm hit");
        user.setNumberOfReviews(0);
        user.setRating(0L);
        user.setVerified(false);
        user.setEmailVerified(true);
        return picture1;
    }

    /**
     * method to get user information form google and log them in to the application
     *
     * @param socialLoginRequest
     *            id of the google user
     *
     * @return returns user login dto and jwt token
     *
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @Override
    public Response loginUserGoogle(SocialLoginRequest socialLoginRequest)
            throws GeneralSecurityException, IOException {
        URL url = new URL("https://oauth2.googleapis.com/tokeninfo?id_token=" + socialLoginRequest.getId_token());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        // "666906861821-cgtait2m7uotr9ra4bm7j6s2hndseoel.apps.googleusercontent.com"

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();

        User user = new User();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
            if (inputLine.contains("\"email\"")) {
                String replace = inputLine.split(":")[1];
                System.out.println(replace = replace.replace("\"", ""));
                user.setEmail(replace.replace(",", ""));
            }
            if (inputLine.contains("\"given_name\"")) {
                String replace = inputLine.split(":")[1];
                System.out.println(replace = replace.replace("\"", ""));
                user.setFirstName(replace.replace(",", ""));
            }
            if (inputLine.contains("\"family_name\"")) {
                String replace = inputLine.split(":")[1];
                System.out.println(replace = replace.replace("\"", ""));
                user.setLastName(replace.replace(",", ""));
            }
            if (inputLine.contains("\"picture\"")) {
                String replace = inputLine.split(":")[1] + inputLine.split(":")[2];
                System.out.println(replace = replace.replace("\"", ""));
                user.setPictureUrl(replace.replace(",", ""));
            }
        }
        in.close();

        con.disconnect();

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(user.getEmail());

        if (userDetails == null) {
            Picture picture = profilePicture(user);
            user.setPassword(passwordEncoder.encode(generatePassword(8)));
            userRepository.save(user);
            pictureRepository.save(picture);
            userDetails = userDetailsServiceImpl.loadUserByUsername(user.getEmail());
        }
        final String token = jwtUtil.generateToken(userDetails);

        user = userRepository.findByEmail(user.getEmail());

        System.out.println(user.getFirstName() + " " + user.getLastName() + " " + user.getEmail());
        System.out.println(user.getId());
        LoginResponse jwt = LoginResponse.builder().id(user.getId()).token(token).build();

        return new Response(jwt, HttpStatus.ACCEPTED);
    }

    /**
     * Helper method to generate password for facebook and google users
     * 
     * @param length
     *            length of password
     * 
     * @return returns raw password
     */
    private String generatePassword(int length) {
        String capitalLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialChar = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalLetters + lowerCaseLetters + specialChar + numbers;
        Random randome = new Random();
        char[] password = new char[length];

        password[0] = lowerCaseLetters.charAt(randome.nextInt(lowerCaseLetters.length()));
        password[1] = capitalLetters.charAt(randome.nextInt(capitalLetters.length()));
        password[2] = specialChar.charAt(randome.nextInt(specialChar.length()));
        password[3] = numbers.charAt(randome.nextInt(numbers.length()));

        for (int i = 4; i < length; i++) {
            password[i] = combinedChars.charAt(randome.nextInt(combinedChars.length()));
        }
        return new String(password);
    }

    /**
     * Method to handle user logging in.
     * 
     * @param loginDto
     *            dto containing login credentials.
     * 
     * @return response.
     */
    @Override
    public Response login(LoginDto loginDto) {

        System.out.println(loginDto.getEmail() + " " + loginDto.getPassword());
        User user = userRepository.findByEmail(loginDto.getEmail());
        if (user == null) {
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

        LoginResponse jwt = LoginResponse.builder().id(user.getId()).token(token).build();

        return new Response(jwt, HttpStatus.ACCEPTED);
    }

    /**
     * Method to handle request of resetting password.
     * 
     * @param forgotPasswordDto
     *            dto containing email.
     * @param url
     *            url to send in the mail of the user.
     * 
     * @return response if mail is sent.
     * 
     * @throws MessagingException
     *             throws exception if messaging fails.
     */
    @Override
    public Response resetPassword(UserForgotPasswordDto forgotPasswordDto, String url)
            throws MessagingException, IOException {
        User user = userRepository.findByEmail(forgotPasswordDto.getEmail());

        if (user != null) {
            String token = UUID.randomUUID().toString();
            ResetPasswordToken resetToken = new ResetPasswordToken(user, token);
            resetPasswordTokenRepository.save(resetToken);

            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getFirstName() + " " + user.getLastName());
            variables.put("url", url + "/auth/renewYourPassword");

            Email email = Email.builder().from("BOCO@gmail.com").to(user.getEmail())
                    .template(new ThymeleafTemplate("verify_mail", variables)).subject("Foresp??rsel om ?? endre passord")
                    .build();
            emailService.sendEmail(email);

            // emailService.sendEmail("BOCO", user.getEmail(), "Konto i BOCO, nytt passord",
            // "Klikk p?? lenken under for ?? endre passordet ditt." + "\n" + url + "/auth/renewYourPassword");//TODO
            // renewYourPassword skal sende bruker til form som skal sende til /renewPassword
            log.info("Click the link to change your account: {}", url + "/auth/renewYourPassword");

            return new Response(token, HttpStatus.ACCEPTED);
        }

        throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Bruker med forgotPasswordDto er ikke funnet!");
    }

    /**
     * Method to validate the password wanted to change by token created.
     * 
     * @param token
     *            token to verify the user.
     * @param forgotPasswordDto
     *            dto containing password to change.
     * 
     * @return ModelAndView with response.
     */
    @Override
    public ModelAndView validatePasswordThroughToken(String token, UserRenewPasswordDto forgotPasswordDto) {
        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByToken(token);
        ModelAndView view = new ModelAndView("verified");
        if (resetPasswordToken.equals(null)) {
            view.addObject("txt1", "Ikke gyldig token for ?? bytte passord!");
            view.addObject("url", "https://localhost:8080/login");
            return view;
        }

        User user = resetPasswordToken.getUser();
        Calendar cal = Calendar.getInstance();

        if ((resetPasswordToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            resetPasswordTokenRepository.delete(resetPasswordToken);
            view.addObject("txt1", "Tidsfristen for ?? endre passord er g??tt ut!!!");
            view.addObject("txt2", "Trykk p?? glemt passord p?? innloggings siden for ?? kunne endre p?? nytt.");
            return view;
        }

        user.setPassword(passwordEncoder.encode(forgotPasswordDto.getPassword()));
        userRepository.save(user);

        view.addObject("txt1", "Vi er glade for at du har registrert deg hos oss");
        view.addObject("txt2", "Du er verifisert og har n?? muligheten til ?? leie.");
        view.addObject("txt3", "Hvis du har tidligere verifisert kontoen din, trenger du ikke ?? gj??re det igjen.");
        return view;
    }

    /**
     * Method to create an account.
     * 
     * @param createAccount
     *            Dto to create an account.
     * @param url
     *            url to send in the mail to user.
     * 
     * @return response.
     */
    @Override
    public Response createUser(CreateAccountDto createAccount, String url) throws MessagingException, IOException {
        if (userRepository.findByEmail(createAccount.getEmail()) != null) {
            return new Response("Mail is already registered", HttpStatus.IM_USED);
        }

        User user = modelMapper.map(createAccount, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        File pb = new File("src/main/resources/static/images/anders.jpg");
        byte[] fileContent = Files.readAllBytes(pb.toPath());
        Picture picture1 = Picture.builder().filename(pb.getName())
                .base64(Base64.getEncoder().encodeToString(fileContent)).type(Files.probeContentType(pb.toPath()))
                .build();
        user.setPicture(picture1);
        picture1.setUser(user);
        pictureRepository.save(picture1);
        user.setNumberOfReviews(0);
        user.setRating(0L);
        user.setVerified(false);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        saveUserVerificationTokenForUser(token, user);

        url = url + "/auth/verifyEmail?token=" + token;

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getFirstName() + " " + user.getLastName());
        variables.put("url", url);
        variables.put("lagd", "sant");

        Email email = Email.builder().from("BOCO@gmail.com").to(user.getEmail())
                .template(new ThymeleafTemplate("verify_mail", variables)).subject("Verifiser konto i BOCO").build();
        emailService.sendEmail(email);

        log.info("Click the link to verify your account: {}", url);

        return new Response("Verifiserings mail er sendt til mailen din !", HttpStatus.CREATED);
    }

    /**
     * Method to validate email by their token.
     * 
     * @param token
     *            token to validate email by.
     * 
     * @return string response if valid or not.
     */
    @Override
    public String validateEmailThroughToken(String token) {
        Optional<UserVerificationToken> verificationTokenOpt = userVerificationTokenRepository.findByToken(token);

        if (verificationTokenOpt.isEmpty()) {
            return "Invalid email";
        }
        UserVerificationToken verificationToken = verificationTokenOpt.get();

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            userVerificationTokenRepository.delete(verificationToken);
            return "Valideringstid utl??pt";
        }

        user.setEmailVerified(true);
        userRepository.save(user);
        return "valid email";
    }

    /**
     * Method to save user verification.
     * 
     * @param token
     *            token to verify user after creation.
     * @param user
     *            user to add token to.
     */
    @Override
    public void saveUserVerificationTokenForUser(String token, User user) {
        UserVerificationToken userVerificationToken = new UserVerificationToken(user, token);

        userVerificationTokenRepository.save(userVerificationToken);
    }

    /**
     * Method to create new token if the previous is expired.
     * 
     * @param prevToken
     *            previous token.
     * @param url
     *            url to send mail to.
     * 
     * @return response.
     * 
     * @throws MessagingException
     *             throws exception if messaging fails.
     */
    @Override
    public Response createNewToken(String prevToken, HttpServletRequest url) throws MessagingException {
        Optional<UserVerificationToken> verificationTokenOpt = userVerificationTokenRepository.findByToken(prevToken);

        if (verificationTokenOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Token not found in repository");
        }
        UserVerificationToken verificationToken = verificationTokenOpt.get();

        verificationToken.setToken(UUID.randomUUID().toString());
        userVerificationTokenRepository.save(verificationToken);
        User user = verificationToken.getUser();

        String newUrl = "https://" + url.getServerName() + ":" + url.getServerPort() + url.getContextPath()
                + "/auth/verifyRegistration?token=" + verificationToken.getToken();

        emailService.sendEmail("BOCO", user.getEmail(), "Konto i BOCO",
                "Kontoen din er nesten klar, " + " klikk p?? lenken under for ?? verifisere kontoen din." + "\n" + url);
        // TODO create own mail

        log.info("Click the link to verify your account: {}", newUrl);
        return new Response("Verifikasjons mail er sendt til din email!", HttpStatus.ACCEPTED);
    }

    /**
     * Method to update auth type of user logging in.
     * 
     * @param email
     *            email.
     * @param oauth2ClientName
     *            name of auth type.
     */
    @Override
    public void updateAuthenticationType(String email, String oauth2ClientName) {
        AuthenticationType authType = AuthenticationType.valueOf(oauth2ClientName.toUpperCase());
        User user = userRepository.findByEmail(email);
        user.setAuthType(authType);
        userRepository.save(user);
        // TODO verify user logging in by facebook and google and put them in repo
    }

    /**
     * Method to generate a JWToken for a user logging in.
     * 
     * @param token
     *            token to verify user.
     * 
     * @return JWToken.
     */
    @Override
    public String getUserJWT(String token) {
        UserVerificationToken verificationToken = userVerificationTokenRepository.findByToken(token).get();
        final UserDetails userDetails = userDetailsServiceImpl
                .loadUserByUsername(verificationToken.getUser().getEmail());

        return jwtUtil.generateToken(userDetails);
    }

}
