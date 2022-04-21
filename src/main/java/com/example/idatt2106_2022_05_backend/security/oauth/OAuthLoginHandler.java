package com.example.idatt2106_2022_05_backend.security.oauth;

import com.example.idatt2106_2022_05_backend.service.authorization.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuthLoginHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        OAuth2UserImpl oauth2User = (OAuth2UserImpl) authentication.getPrincipal();
        String oauth2ClientName = oauth2User.getOauth2ClientName();
        String username = oauth2User.getEmail();

        authService.updateAuthenticationType(username, oauth2ClientName);

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
