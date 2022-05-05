package com.example.idatt2106_2022_05_backend.security;

import com.example.idatt2106_2022_05_backend.dto.ReviewDto;
import com.example.idatt2106_2022_05_backend.model.User;
import org.springframework.stereotype.Service;

/**
 * Service class to
 */
@Service
public interface SecurityService {

    boolean isUser(Long userId);

    boolean isAdOwner(Long adId);

    boolean isVerifiedUser(Long userId);

    boolean isRentalOwner(Long rentalId);

    boolean isRentalOwnerByToken(Long rentalId, String token);

    boolean isRentalBorrower(Long rentalId);

    User getCurrentUser();

    boolean userPicture(long id, long userId);

    boolean isUserByEmail(String borrower);

    boolean isReviewOwner(ReviewDto reviewDto);

    // boolean userRentalAccess(Long userId);

}
