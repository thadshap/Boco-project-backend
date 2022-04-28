package com.example.idatt2106_2022_05_backend.service.user;

import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * UserDetailsService instance personalized
 */
@Slf4j
@Service
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public UserDetailsServiceImpl() {
        super();
    }

    /**
     * this method retrieves a user by their email
     * 
     * @param email
     *            email of the user
     * 
     * @return returns a {@link UserDetails} object
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        if (userRepository.findByEmail(email) == null) {
            // throw new IllegalArgumentException();
            System.out.println("user does not exist");
            return null;
        }
        User user = userRepository.findByEmail(email);

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                new ArrayList<>());
    }
}
