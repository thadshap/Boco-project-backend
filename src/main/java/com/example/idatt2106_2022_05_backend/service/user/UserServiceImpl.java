package com.example.idatt2106_2022_05_backend.service.user;

import com.example.idatt2106_2022_05_backend.dto.PictureReturnDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserReturnDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Service class to handle user objects
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AdService adService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Method to delete user from repository.
     *
     * @param userId
     *            user id to delete user.
     *
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response deleteUser(Long userId) {
        // Find the user
        Optional<User> userFound = userRepository.findById(userId);
        if (userFound.isPresent()) {
            // Retrieve the user
            User user = userFound.get();

            // Get the ads
            Set<Ad> ads = user.getAds();
            if (ads != null) {
                // Iterate over all the ads and delete them
                for (Ad ad : ads) {
                    adService.deleteAd(ad.getId());
                }
            }
            // Delete ads from user
            user.setAds(null);

            // Get the rentals
            List<Rental> rentals = user.getRentalsOwned();
            if (rentals != null) {
                for (Rental rental : rentals) {
                    rental.setOwner(null);
                    rentalRepository.save(rental);
                }
            }

            // Delete rentals from user
            user.setRentalsOwned(null);

            List<Rental> rentals2 = user.getRentalsBorrowed();
            if (rentals != null) {
                for (Rental rental : rentals2) {
                    rental.setBorrower(null);
                    rentalRepository.save(rental);
                }
            }

            // Delete rentals from user
            user.setRentalsBorrowed(null);

            // Get the reviews
            List<Review> reviews = user.getReviews();
            if (reviews != null) {
                for (Review review : reviews) {
                    review.setUser(null);
                    reviewRepository.save(review);
                }
            }

            // Delete reviews from user
            user.setReviews(null);

            /**
             * // Get the output-messages List<OutputMessage> messages = ouputMessageRepository.findAll();
             * for(OutputMessage message : messages) { if(Objects.equals(message.getUser().getId(), user.getId())) {
             * message.setUser(null); message.setGroup(null); ouputMessageRepository.save(message);
             * ouputMessageRepository.delete(message); } }
             *
             * Set<OutputMessage> outputMessages = user.getMessages(); if(outputMessages != null) { for(OutputMessage
             * message : outputMessages) { message.setUser(null); ouputMessageRepository.save(message);
             * ouputMessageRepository.delete(message); } }
             */
            // Get messages
//            List<Message> messages = messageRepository.findAll();
//            for (Message message : messages) {
//                if (message.getUser().getId() == user.getId()) {
//                    message.setUser(null);
//                    messageRepository.save(message);
//                }
//            }


            userRepository.save(user);

            // Delete the user
            userRepository.deleteById(user.getId());

            return new Response("User deleted", HttpStatus.ACCEPTED);
        } else {
            return new Response("User not found", HttpStatus.NOT_FOUND);
        }

    }

    /**
     * method to delete a picture on an ad
     *
     * @param userId
     *            the id of the user that wished to change their profile picture
     * @param chosenPicture
     *            the picture to remove (converted to bytes)
     *
     * @return response with status ok or not found
     */
    @Override
    public Response deleteProfilePicture(long userId, byte[] chosenPicture) {
        Optional<User> userFound = userRepository.findById(userId);

        // If present
        if (userFound.isPresent()) {

            // Get the user
            User user = userFound.get();

            // If the user has a profile picture
            if(user.getPicture() != null) {

                // Get the profile picture
                Picture profilePicture = user.getPicture();

                // Check to see if the profile picture is the same as the argument
                if (Arrays.equals(profilePicture.getData(), chosenPicture)) {

                    // Remove this picture from user
                    user.setPicture(null); // todo maybe to this about 10 lines below?
                    userRepository.save(user);

                    // Set the foreign keys of the picture equal to null
                    profilePicture.setAd(null);
                    profilePicture.setUser(null);
                    pictureRepository.save(profilePicture);

                    // Delete the PICTURE
                    pictureRepository.delete(profilePicture);

                    // Update the user //todo or take this first
                    userRepository.save(user);

                    return new Response("Slettet bildet", HttpStatus.OK);
                }
                else {
                    // If we get here, pictures are equal to null
                    return new Response("Denne brukeren har et annet profilbilde", HttpStatus.NOT_FOUND);
                }
            }
            // If we get here, pictures are equal to null
            return new Response("Denne brukeren har ikke profilbilde", HttpStatus.NO_CONTENT);
        }
        return new Response("Bruker med spesifisert ID ikke funnet", HttpStatus.NO_CONTENT);
    }

    /**
     * Method to update to update profile picture.
     * @param userId id of user.
     * @param file picture file.
     * @return response if user exists or not.
     * @throws IOException when retrieving bytes from file.
     */
    @Override
    public Response updatePicture(Long userId, MultipartFile file) throws IOException {
        // User user = userRepository.getById(userId);
        Optional<User> userFound = userRepository.findById(userId);

        if(userFound.isPresent()) {
            // Get the user
            User user = userFound.get();

            // Create the picture entity using the multipart-file
            Picture picture = Picture.builder()
                    .filename(file.getName()).type(file.getContentType())
//                    .data(file.getBytes())
                    .base64(Base64.getEncoder().encodeToString(file.getBytes()))
                    .build();

            if(user.getPicture() != null) {
                // Delete the current photo
                pictureRepository.findById(user.getPicture().getId()).get().setUser(null);
                user.setPicture(null);
                userRepository.save(user);
                pictureRepository.deleteByUser(user);

                // Set the new photo
                user.setPicture(picture);
                picture.setUser(user);
                userRepository.save(user);
                pictureRepository.save(picture);
            }
            else {
                user.setPicture(picture);
                picture.setUser(user);
                userRepository.save(user);
                pictureRepository.save(picture);
            }
            return new Response("Bildet er lagret", HttpStatus.OK);
        }
        else {
            return new Response("Brukeren ble ikke funnet", HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Method to return picture of user.
     * @param userId id of user.
     * @return returns user profile picture.
     */
    @Override
    public PictureReturnDto getPicture(Long userId) {
        User user = userRepository.getById(userId);
        List<Picture> picture = pictureRepository.findByUser(user);
        //temp fix so backend does not crash when no picture found
        if (picture.size() == 0) {
            return null;
        }
        return PictureReturnDto.builder()
//                .base64(Base64.getEncoder().encodeToString(picture.get(0).getData()))
                .base64(picture.get(0).getBase64())
                .type(picture.get(0).getType())
                .build();
    }

    /**
     * Method to update User object in the repository.
     *
     * @param userId
     *            id of the user to update.
     * @param userUpdateDto
     *            {@link UserUpdateDto} object with variables to update user.
     *
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response updateUser(Long userId, UserUpdateDto userUpdateDto) throws IOException {
        Optional<User> userFromDB = userRepository.findById(userId);
        if (userFromDB.isEmpty()) {
            return new Response("User not found", HttpStatus.NO_CONTENT);
        }
        User user = userFromDB.get();
        if (userUpdateDto.getFirstName() != null) {
            if (!userUpdateDto.getFirstName().isEmpty() || !userUpdateDto.getFirstName().isBlank()) {
                user.setFirstName(userUpdateDto.getFirstName());
            }
        }
        if (userUpdateDto.getLastName() != null) {
            if (!userUpdateDto.getLastName().isEmpty() || !userUpdateDto.getLastName().isBlank()) {
                user.setLastName(userUpdateDto.getLastName());
            }
        }
        if (userUpdateDto.getPassword() != null) {
            if (!userUpdateDto.getPassword().isEmpty() || !userUpdateDto.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
            }
        }
        // if all fields are empty
        else if(     userUpdateDto.getFirstName() == null &&
                userUpdateDto.getLastName() == null &&
                userUpdateDto.getPassword() == null) {
            return null;
        }
        return new Response("User updated", HttpStatus.OK);
    }

    /**
     * Method to retrieve user from the database.
     *
     * @param userId
     *            id of user to retrieve.
     *
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response getUser(Long userId) {
        Optional<User> userFromDB = userRepository.findById(userId);
        if (userFromDB.isEmpty()) {
            return new Response("User not found", HttpStatus.NO_CONTENT);
        }
        User userGot = userFromDB.get();
        if (userGot.getRating() > 8){
            userGot.setVerified(true);
        }
        UserReturnDto user = UserReturnDto.builder()
                .id(userGot.getId())
                .firstName(userGot.getFirstName())
                .lastName(userGot.getLastName())
                .email(userGot.getEmail())
                .role(userGot.getRole())
                .verified(userGot.isVerified())
                .rating(userGot.getRating())
                .nrOfReviews(userGot.getNumberOfReviews())
                .pictureUrl(userGot.getPictureUrl())
                .build();

        return new Response(user, HttpStatus.OK);
    }

}
