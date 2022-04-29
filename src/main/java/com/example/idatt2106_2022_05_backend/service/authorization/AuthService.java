package com.example.idatt2106_2022_05_backend.service.authorization;

import com.example.idatt2106_2022_05_backend.dto.user.CreateAccountDto;
import com.example.idatt2106_2022_05_backend.dto.user.LoginDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserForgotPasswordDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserRenewPasswordDto;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

public interface AuthService {

    String getFacebookUrl();

    ModelAndView forwardToFacebook(String authorizationCode);

    String getGoogleUrl();

    ModelAndView forwardToGoogle(String authorizationCode);

    Response createUser(CreateAccountDto createAccount, String url);

    void saveUserVerificationTokenForUser(String token, User user);

    String validateEmailThroughToken(String token);

    Response createNewToken(String prevToken, HttpServletRequest url) throws MessagingException;

    Response resetPassword(UserForgotPasswordDto forgotPasswordDto, String url) throws MessagingException;

    Response validatePasswordThroughToken(String token, UserRenewPasswordDto forgotPasswordDto);

    Response login(LoginDto loginDto) throws Exception;

    void updateAuthenticationType(String username, String database);

    String getUserJWT(String token);
}
