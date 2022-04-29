package com.example.idatt2106_2022_05_backend.security;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Rental;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.google.api.plus.Activity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SecurityService {

    UserRepository userRepository;

    AdRepository adRepository;

    RentalRepository rentalRepository;

    ReviewRepository reviewRepository;

    PictureRepository pictureRepository;

    private User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = authentication != null ? (UserDetails) authentication.getPrincipal() : null;
        String email = userDetails != null ? userDetails.getUsername() : "";
        return userRepository.findByEmail(email);
    }

    public boolean isUser(Long userId){
        User user = getUser();
        return user != null && user.getId().equals(userId);
    }

    private boolean isAdOwner(Long adId){
        Ad ad = adRepository.findById(adId).orElse(null);
        User user = getUser();
        if(ad != null && user != null  && ad.getUser() != null){
            return ad.getUser().equals(user);
        }
        return false;
    }

    public  boolean userRentalAccess(Long userId){
        User user = getUser();
//        Rental activity = rentalRepository.findById(activityId).orElse(null);
//        if(activity != null && user != null ){
//            return hasActivityAccess(activity,user);
//        }
        return false;
    }
}
