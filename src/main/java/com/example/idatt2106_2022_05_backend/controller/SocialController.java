package com.example.idatt2106_2022_05_backend.controller;

import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/signin")
public class SocialController {

    private FacebookConnectionFactory factory = new FacebookConnectionFactory("1181763609285094",
            "822eef3823b53888eb4dd9f0c1a09463"
            );


//	public ModelAndView firstPage() {
//		return new ModelAndView("welcome");
//	}

    @GetMapping(value = "/facebook")
    public String producer() {

        OAuth2Operations operations = factory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();

        params.setRedirectUri("http://localhost:8080/forwardLogin");
        params.setScope("email,public_profile");

        String url = operations.buildAuthenticateUrl(params);
        System.out.println("The URL is" + url);
        return "redirect:" + url;

    }

    @RequestMapping(value = "/forwardLogin")
    public ModelAndView prodducer(@RequestParam("code") String authorizationCode) {
        OAuth2Operations operations = factory.getOAuthOperations();
        AccessGrant accessToken = operations.exchangeForAccess(authorizationCode, "http://localhost:8080/forwardLogin",
                null);

        Connection<Facebook> connection = factory.createConnection(accessToken);
        Facebook facebook = connection.getApi();
        String[] fields = { "id", "email", "first_name", "last_name" };
        User userProfile = facebook.fetchObject("me", User.class, fields);
        ModelAndView model = new ModelAndView("details");
        model.addObject("user", userProfile);
        return model;

    }

}
