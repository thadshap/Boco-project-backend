package com.example.idatt2106_2022_05_backend.security;

import com.example.idatt2106_2022_05_backend.service.authorization.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class DatabaseLoginHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	AuthService authService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		authService.updateAuthenticationType(userDetails.getUsername(), "database");
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
