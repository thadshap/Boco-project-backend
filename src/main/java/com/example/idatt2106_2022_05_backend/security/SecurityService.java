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

/**
 * Service class to
 */
@Service
public interface SecurityService {

    boolean isUser(Long userId);

    boolean isAdOwner(Long adId);

    boolean isVerifiedUser(Long userId);

    boolean isRentalOwner(Long rentalId);

    boolean isRentalBorrower(Long rentalId);

    public User getCurrentUser();

    boolean userPicture(long id, long userId);

//    boolean userRentalAccess(Long userId);

}
