package com.example.idatt2106_2022_05_backend.security;

import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service class to verify user access.
 */
@Service
public class SecurityServiceimpl implements SecurityService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AdRepository adRepository;

    @Autowired
    RentalRepository rentalRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    PictureRepository pictureRepository;

    @Autowired
    private AcceptRentalTokenRepository acceptRentalTokenRepository;

    /**
     * Method to see if user that is making the request.
     * @return user if exists in repo else null.
     */
    private User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = authentication != null ? (UserDetails) authentication.getPrincipal() : null;
        String email = userDetails != null ? userDetails.getUsername() : "";
        return userRepository.findByEmail(email);
    }

    /**
     * Method to get the current user.
     * @return current user making the call.
     */
    @Override
    public User getCurrentUser(){
        return getUser();
    }

    /**
     * Method to see if userId is the same as user making requests.
     * @param userId id of user.
     * @return true if user id is the same as requester.
     */
    @Override
    public boolean isUser(Long userId){
        User user = getUser();
        return user != null && user.getId().equals(userId);
    }

    /**
     * Method to see if owner of the ad is making the request.
     * @param adId id of the ad.
     * @return true is user is the owner of the ad.
     */
    @Override
    public boolean isAdOwner(Long adId){
        Ad ad = adRepository.findById(adId).orElse(null);
        User user = getUser();
        if(ad != null && user != null  && ad.getUser() != null){
            return ad.getUser().equals(user);
        }
        return false;
    }

    /**
     * Method to see if user is verified.
     * @param userId id of the user.
     * @return returns true if user is verified.
     */
    @Override
    public boolean isVerifiedUser(Long userId){
        User user = getUser();
        return user.isVerified();
    }

    /**
     * Method to see if user is owner of rental object.
     * @param rentalId id of rental object.
     * @return true if user is owner of rental object.
     */
    @Override
    public boolean isRentalOwner(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElse(null);
        User user = getUser();
        if(rental != null && user != null  && rental.getOwner() != null){
            return rental.getOwner().equals(user);
        }
        return false;
    }

    /**
     * Method to see if user is owner of rental object by a string token.
     * @param rentalId id of rental object.
     * @return true if user is owner of rental object.
     */
    @Override
    public boolean isRentalOwnerByToken(Long rentalId, String token) {
        Rental rental = rentalRepository.findById(rentalId).orElse(null);
        AcceptRentalToken accToken = acceptRentalTokenRepository.findByToken(token);
        User user = accToken.getUser();
        if(rental != null && user != null  && rental.getOwner() != null){
            return rental.getOwner().equals(user);
        }
        return false;
    }

    /**
     * Method to see if user is borrower of rental object.
     * @param rentalId id of rental object.
     * @return true if user is borrower of rental object.
     */
    @Override
    public boolean isRentalBorrower(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElse(null);
        User user = getUser();
        if(rental != null && user != null  && rental.getBorrower() != null){
            return rental.getBorrower().equals(user);
        }
        return false;
    }

    /**
     * Method to see if the picture belongs to the user.
     * @param id id of the picture.
     * @param userId id of the user.
     * @return true if picture belongs to the user.
     */
    @Override
    public boolean userPicture(long id, long userId) {
        Picture picture = pictureRepository.findById(id).orElse(null);
        User user = getUser();
        if(picture != null && user != null  && picture.getUser() != null){
            return picture.getUser().equals(user);
        }
        return false;
    }

    /**
     * Method to see if userId is the same as user making requests.
     * @param borrower email of the borrower.
     * @return true if user id is the same as requester.
     */
    @Override
    public boolean isUserByEmail(String borrower) {
        User user = getUser();
        return user != null && user.getEmail().equals(borrower);
    }

//    /**
//     * Method to see if user has access to activate rental object.
//     * @param rentalId id of user.
//     * @return true if user has access.
//     */
//    @Override
//    public boolean userRentalAccess(Long userId){
//        User user = getUser();
//        Rental rental = rentalRepository.findByOwner(activityId);
//        if(rental != null && user != null ){
//            return hasActivityAccess(rental,user);
//        }
//        return false;
//    }
}
