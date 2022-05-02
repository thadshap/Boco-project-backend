package com.example.idatt2106_2022_05_backend.service.authorization;

import com.example.idatt2106_2022_05_backend.dto.user.*;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.model.google.GoogleSignin;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;

public interface AuthService {

    Response loginUserFacebook(String accessToken);

    Response loginUserGoogle(SocialLoginRequest socialLoginRequest) throws GeneralSecurityException, IOException;

    Response login(LoginDto loginDto) throws Exception;

    Response resetPassword(UserForgotPasswordDto forgotPasswordDto, String url) throws MessagingException, IOException;

    ModelAndView validatePasswordThroughToken(String token, UserRenewPasswordDto forgotPasswordDto);

    Response createUser(CreateAccountDto createAccount, String url);

    String validateEmailThroughToken(String token);

    void saveUserVerificationTokenForUser(String token, User user);

    Response createNewToken(String prevToken, HttpServletRequest url) throws MessagingException;

    void updateAuthenticationType(String username, String database);

    String getUserJWT(String token);
}
