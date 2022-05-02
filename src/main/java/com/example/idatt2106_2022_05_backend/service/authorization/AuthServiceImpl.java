package com.example.idatt2106_2022_05_backend.service.authorization;

import com.example.idatt2106_2022_05_backend.dto.LoginResponse;
import com.example.idatt2106_2022_05_backend.dto.user.*;
import com.example.idatt2106_2022_05_backend.enums.AuthenticationType;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.model.facebook.FacebookUser;
import com.example.idatt2106_2022_05_backend.model.google.GoogleSignin;
import com.example.idatt2106_2022_05_backend.repository.ResetPasswordTokenRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.repository.UserVerificationTokenRepository;
import com.example.idatt2106_2022_05_backend.security.JWTUtil;
import com.example.idatt2106_2022_05_backend.security.SecurityService;
import com.example.idatt2106_2022_05_backend.service.email.EmailService;
import com.example.idatt2106_2022_05_backend.service.user.UserDetailsServiceImpl;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
import com.example.idatt2106_2022_05_backend.util.Response;
import com.example.idatt2106_2022_05_backend.util.registration.RegistrationComplete;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.*;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.testing.http.MockHttpTransport;
import com.nimbusds.jose.Payload;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.*;

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

    private FacebookConnectionFactory facebookFactory = new FacebookConnectionFactory("1181763609285094",
            "822eef3823b53888eb4dd9f0c1a09463"
    );

    private GoogleConnectionFactory googleFactory = new GoogleConnectionFactory(
            "292543393372-pafvrt6kltssgf27g4p6safka8oqmgud.apps.googleusercontent.com",
            "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"
    );

    /**
     * Method to return the url to login with facebook.
     * @return url to login with facebook.
     */
    @Override
    public String getFacebookUrl() {
        OAuth2Operations operations = facebookFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();

        params.setRedirectUri("http://localhost:8443/auth/forwardLogin/facebook");
        params.setScope("email,public_profile");
        //TODO thymeleaf

        return  operations.buildAuthenticateUrl(params);
    }

    /**
     * Method to retrieve user information from facebook.
     * @param authorizationCode authorization code from facebook login.
     * @return redirection view to frontend.
     */
    @Override
    public RedirectView forwardToFacebook(String authorizationCode) {
        OAuth2Operations operations = facebookFactory.getOAuthOperations();
        AccessGrant accessToken = operations.exchangeForAccess(authorizationCode, "http://localhost:8443/auth/forwardLogin/facebook",
                null);

        Connection<Facebook> connection = facebookFactory.createConnection(accessToken);
        Facebook facebook = connection.getApi();
        String[] fields = { "id", "email", "first_name", "last_name" };

        org.springframework.social.facebook.api.User userProfile =
                facebook.fetchObject("me", org.springframework.social.facebook.api.User.class, fields);

        System.out.println(userProfile.getId() + " " + userProfile.getEmail() + ", " + userProfile.getFirstName() + " " + userProfile.getLastName());

        return new RedirectView("http://localhost:8443/auth/login/?email=" + userProfile.getEmail());
    }

    /**
     * Method to return the url to login with google.
     * @return url to login with google.
     */
    @Override
    public String getGoogleUrl() {
        OAuth2Operations operations = googleFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();

        params.setRedirectUri("http://localhost:8443/auth/forwardLogin/google");
        params.setScope("email");
        //TODO thymeleaf

        return  operations.buildAuthenticateUrl(params);
    }

    /**
     * Method to retrieve user information from google.
     * @param authorizationCode authorization code from google login.
     * @return redirection view to frontend.
     */
    @Override
    public RedirectView forwardToGoogle(String authorizationCode) {
        OAuth2Operations operations = googleFactory.getOAuthOperations();
        AccessGrant accessToken = operations.exchangeForAccess(authorizationCode, "http://localhost:8443/auth/forwardLogin/google",
                null);

        Connection<Google> connection = googleFactory.createConnection(accessToken);
        Google google = connection.getApi();
        String[] fields = { "id", "email", "first_name", "last_name" };

        org.springframework.social.google.api.plus.Person userProfile =
                google.plusOperations().getGoogleProfile();
//                        .fetchObject("me", org.springframework.social.google.api.userinfo.implclass, fields);
//        ModelAndView model = new ModelAndView("details");

        System.out.println(userProfile.getId() + " " + userProfile.getDisplayName() + ", " + userProfile.getEmailAddresses().iterator().next());

        return new RedirectView("https://localhost:8080/login/google/" + userProfile.getId());
    }

    @Autowired private FacebookClient facebookClient;

    @Override
    public Response loginUserFacebook(String accessToken) {
        FacebookUser facebookUser = facebookClient.getUser(accessToken);

        System.out.println(facebookUser.getEmail() + " " + facebookUser.getFirstName() + " " + facebookUser.getLastName() + " " +
                facebookUser.getPicture() + " " + facebookUser.getEmail());

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(facebookUser.getEmail());

        if (userDetails == null){
            System.out.println("user details is null");
            User user = User.builder()
                    .email(facebookUser.getEmail())
                    .firstName(facebookUser.getFirstName())
                    .lastName(facebookUser.getLastName())
                    .verified(true)
                    .password(passwordEncoder.encode(generatePassword(8)))
                    .build();
            userRepository.save(user);
            userDetails = userDetailsServiceImpl.loadUserByUsername(facebookUser.getEmail());
            System.out.println("user is saved");
            System.out.println(userDetails.getUsername());
        }
        final String token = jwtUtil.generateToken(userDetails);
        System.out.println(userDetails.getUsername());
        System.out.println(token);
        User user = userRepository.findByEmail(facebookUser.getEmail());
        System.out.println(user.getEmail());
        LoginResponse jwt = LoginResponse.builder()
                .id(user.getId())
                .token(token)
                .build();

        return new Response(jwt, HttpStatus.ACCEPTED);
    }

    @Override
    public Response loginUserGoogle(SocialLoginRequest socialLoginRequest) throws GeneralSecurityException, IOException {
        URL url = new URL("https://oauth2.googleapis.com/tokeninfo?id_token=" + socialLoginRequest.getId_token());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();

        User user = new User();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
            if (inputLine.contains("\"email\"")){
                String replace = inputLine.split(":")[1];
                System.out.println(replace = replace.replace("\"", ""));
                user.setEmail(replace.replace(",", ""));
            }
            if (inputLine.contains("\"given_name\"")){
                String replace = inputLine.split(":")[1];
                System.out.println(replace = replace.replace("\"", ""));
                user.setFirstName(replace.replace(",", ""));
            }
            if (inputLine.contains("\"family_name\"")){
                String replace = inputLine.split(":")[1];
                System.out.println(replace = replace.replace("\"", ""));
                user.setLastName(replace.replace(",", ""));
            }
            if (inputLine.contains("\"picture\"")){
                String replace = inputLine.split(":")[1] + inputLine.split(":")[2];
                System.out.println(replace = replace.replace("\"", ""));
                user.setPictureUrl(replace.replace(",", ""));
            }
        }
        in.close();

        con.disconnect();

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(user.getEmail());

        if (userDetails == null){
            user.setPassword(passwordEncoder.encode(generatePassword(8)));
            user.setVerified(true);
            userRepository.save(user);
            userDetails = userDetailsServiceImpl.loadUserByUsername(user.getEmail());
        }
        final String token = jwtUtil.generateToken(userDetails);

//        User user = userRepository.findByEmail(socialLoginRequest.getEmail());

        System.out.println(user.getFirstName() + " " + user.getLastName() + " " + user.getEmail());

        LoginResponse jwt = LoginResponse.builder()
                .id(user.getId())
                .token(token)
                .build();

        return new Response(jwt, HttpStatus.ACCEPTED);
    }


    private String generatePassword(int length) {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[length];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for(int i = 4; i< length ; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return new String(password);
    }








    /**
     * Method to handle user logging in.
     * @param loginDto dto containing login credentials.
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

        LoginResponse jwt = LoginResponse.builder()
                .id(user.getId())
                .token(token)
                .build();

        return new Response(jwt, HttpStatus.ACCEPTED);
    }

    /**
     * Method to handle request of resetting password.
     * @param forgotPasswordDto dto containing email.
     * @param url url to send in the mail of the user.
     * @return response if mail is sent.
     * @throws MessagingException throws exception if messaging fails.
     */
    @Override
    public Response resetPassword(UserForgotPasswordDto forgotPasswordDto, String url) throws MessagingException, IOException {
        User user = userRepository.findByEmail(forgotPasswordDto.getEmail());

        if (user != null) {
            String token = UUID.randomUUID().toString();
            ResetPasswordToken resetToken = new ResetPasswordToken(user, token);
            resetPasswordTokenRepository.save(resetToken);

            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getFirstName() + " " + user.getLastName());
            variables.put("url", url + "/auth/renewYourPassword");

            Email email = Email.builder()
                    .from("BOCO@gmail.com")
                    .to(user.getEmail())
                    .template(new ThymeleafTemplate("verify_mail", variables))
                    .subject("Forespørsel om å endre passord")
                    .build();
            emailService.sendEmail(email);

//            emailService.sendEmail("BOCO", user.getEmail(), "Konto i BOCO, nytt passord",
//                    "Klikk på lenken under for å endre passordet ditt." + "\n" + url + "/auth/renewYourPassword");//TODO renewYourPassword skal sende bruker til form som skal sende til /renewPassword
            log.info("Click the link to change your account: {}", url + "/auth/renewYourPassword");

            return new Response(token, HttpStatus.ACCEPTED);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bruker med forgotPasswordDto er ikke funnet!");
    }

    /**
     * Method to validate the password wanted to change by token created.
     * @param token token to verify the user.
     * @param forgotPasswordDto dto containing password to change.
     * @return ModelAndView with response.
     */
    @Override
    public ModelAndView validatePasswordThroughToken(String token, UserRenewPasswordDto forgotPasswordDto) {
        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByToken(token);
        ModelAndView view = new ModelAndView("verified");
        if (resetPasswordToken == null) {
            view.addObject("txt1", "Ikke gyldig token for å bytte passord!");;
            return view;
        }

        User user = resetPasswordToken.getUser();
        Calendar cal = Calendar.getInstance();

        if ((resetPasswordToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            resetPasswordTokenRepository.delete(resetPasswordToken);
            view.addObject("txt1", "Tidsfristen for å endre passord er gått ut!!!");
            view.addObject("txt2", "Trykk på glemt passord på innloggings siden for å kunne endre på nytt.");
            return view;
        }

        user.setPassword(passwordEncoder.encode(forgotPasswordDto.getPassword()));
        userRepository.save(user);

        view.addObject("txt1", "Vi er glade for at du har registrert deg hos oss");
        view.addObject("txt2", "Du er verifisert og har nå muligheten til å leie.");
        view.addObject("txt3", "Hvis du har tidligere verifisert kontoen din, trenger du ikke å gjøre det igjen.");
        return view;
    }

    /**
     * Method to create an account.
     * @param createAccount Dto to create an account.
     * @param url url to send in the mail to user.
     * @return response.
     */
    @Override
    public Response createUser(CreateAccountDto createAccount, String url) {
        if (userRepository.findByEmail(createAccount.getEmail()) != null) {
            return new Response("Mail is already registered", HttpStatus.IM_USED);
        }

        User user = modelMapper.map(createAccount, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        publisher.publishEvent(new RegistrationComplete(user, url));

        return new Response("Verifiserings mail er sendt til mailen din !", HttpStatus.CREATED);
    }

    /**
     * Method to validate email by their token.
     * @param token token to validate email by.
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
            return "Valideringstid utløpt";
        }

        user.setVerified(true);
        userRepository.save(user);
        return "valid email";
    }

    /**
     * Method to save user verification.
     * @param token token to verify user after creation.
     * @param user user to add token to.
     */
    @Override
    public void saveUserVerificationTokenForUser(String token, User user) {
        UserVerificationToken userVerificationToken = new UserVerificationToken(user, token);

        userVerificationTokenRepository.save(userVerificationToken);
    }

    /**
     * Method to create new token if the previous is expired.
     * @param prevToken previous token.
     * @param url url to send mail to.
     * @return response.
     * @throws MessagingException throws exception if messaging fails.
     */
    @Override
    public Response createNewToken(String prevToken, HttpServletRequest url) throws MessagingException {
        Optional<UserVerificationToken> verificationTokenOpt = userVerificationTokenRepository.findByToken(prevToken);

        if (verificationTokenOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found in repository");
        }
        UserVerificationToken verificationToken = verificationTokenOpt.get();

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

    /**
     * Method to update auth type of user logging in.
     * @param email email.
     * @param oauth2ClientName name of auth type.
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
     * @param token token to verify user.
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
