package com.example.idatt2106_2022_05_backend.security.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class OAuth2UserImpl implements org.springframework.security.oauth2.core.user.OAuth2User {
	private String oauth2ClientName;
	private org.springframework.security.oauth2.core.user.OAuth2User oauth2User;
	
	public OAuth2UserImpl(org.springframework.security.oauth2.core.user.OAuth2User oauth2User, String oauth2ClientName) {
		this.oauth2User = oauth2User;
		this.oauth2ClientName = oauth2ClientName;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return oauth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return oauth2User.getAuthorities();
	}

	@Override
	public String getName() {
		System.out.println(oauth2User.<String>getAttribute("email"));
		return oauth2User.getAttribute("name");
	}

	public String getEmail() {
		return oauth2User.<String>getAttribute("email");		
	}

	public String getOauth2ClientName() {
		return this.oauth2ClientName;
	}
}
