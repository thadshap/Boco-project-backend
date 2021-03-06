package com.example.idatt2106_2022_05_backend.service.authorization;

import com.example.idatt2106_2022_05_backend.model.facebook.FacebookUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to connect with facebook api and retrieve the user.
 */
@Service
public class FacebookClient {

    @Autowired
    private RestTemplate restTemplate;

    private final String FACEBOOK_GRAPH_API_BASE = "https://graph.facebook.com";

    /**
     * Class to connect with facebook api and retrieve the user.
     * 
     * @param accessToken
     *            token for user given out by facebook.
     * 
     * @return returns an instance of facebook user.
     */
    public FacebookUser getUser(String accessToken) {
        // var path = "/me?fields={fields}&redirect={redirect}&access_token={access_token}";
        String fields = "email,first_name,last_name,id,picture.width(720).height(720)";
        String path = String.format("/me?fields=%s&redirect=%s&access_token=%s", fields, false, accessToken);
        final Map<String, String> variables = new HashMap<>();
        variables.put("fields", fields);
        variables.put("redirect", "false");
        variables.put("access_token", accessToken);
        return restTemplate.getForObject(FACEBOOK_GRAPH_API_BASE + path, FacebookUser.class, variables);
    }
}
