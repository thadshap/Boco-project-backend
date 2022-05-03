package com.example.idatt2106_2022_05_backend.service.user;

import com.example.idatt2106_2022_05_backend.dto.PictureReturnDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserReturnDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.PictureUtility;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
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
    private PictureUtility pictureService;

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
    private OuputMessageRepository ouputMessageRepository; // TODO wrong name
    @Autowired
    private AdService adService;

    private ModelMapper modelMapper = new ModelMapper();

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
        if(userFound.isPresent()) {
            // Retrieve the user
            User user = userFound.get();

            // Get the ads
            Set<Ad> ads = user.getAds();
            if(ads != null) {
                // Iterate over all the ads and delete them
                for(Ad ad : ads) {
                    adService.deleteAd(ad.getId());
                }
            }
            // Delete ads from user
            user.setAds(null);

            // Get the rentals
            List<Rental> rentals = user.getRentalsOwned();
            if(rentals != null) {
                for(Rental rental : rentals) {
                    rental.setOwner(null);
                    rentalRepository.save(rental);
                }
            }

            // Delete rentals from user
            user.setRentalsOwned(null);

            List<Rental> rentals2 = user.getRentalsBorrowed();
            if(rentals != null) {
                for(Rental rental : rentals2) {
                    rental.setBorrower(null);
                    rentalRepository.save(rental);
                }
            }

            // Delete rentals from user
            user.setRentalsBorrowed(null);

            // Get the reviews
            Set<Review> reviews = user.getReviews();
            if(reviews != null) {
                for(Review review : reviews) {
                    review.setUser(null);
                    reviewRepository.save(review);
                }
            }


            // Delete reviews from user
            user.setReviews(null);

            /**
            // Get the output-messages
            List<OutputMessage> messages = ouputMessageRepository.findAll();
            for(OutputMessage message : messages) {
                if(Objects.equals(message.getUser().getId(), user.getId())) {
                    message.setUser(null);
                    message.setGroup(null);
                    ouputMessageRepository.save(message);
                    ouputMessageRepository.delete(message);
                }
            }

            Set<OutputMessage> outputMessages = user.getMessages();
            if(outputMessages != null) {
                for(OutputMessage message : outputMessages) {
                    message.setUser(null);
                    ouputMessageRepository.save(message);
                    ouputMessageRepository.delete(message);
                }
            }
             */
            // Get messages
            List<Message> messages = messageRepository.findAll();
            for(Message message : messages) {
                if(message.getUser().getId() == user.getId()) {
                    message.setUser(null);
                    messageRepository.save(message);
                }
            }

            user.setMessages(null);
            userRepository.save(user);

            // Delete the user
            userRepository.deleteById(user.getId());

            return new Response("User deleted", HttpStatus.ACCEPTED);
        }
        else {
            return new Response("User not found", HttpStatus.NOT_FOUND);
        }

    }

    /**
     * method to delete a picture on an ad
     *
     * @param userId the id of the user that wished to change their profile picture
     * @param chosenPicture the picture to remove (converted to bytes)
     *
     * @return response with status ok or not found
     */
    @Override
    public Response deleteProfilePicture(long userId, byte[] chosenPicture){
        Optional<User> user = userRepository.findById(userId);

        // If present
        if(user.isPresent()) {
            Picture profilePicture = user.get().getPicture();
            if(profilePicture != null) {
                if(Arrays.equals(profilePicture.getData(), chosenPicture)) {

                    // Remove this picture from user
                    user.get().setPicture(null);
                    userRepository.save(user.get());

                    // Set the foreign keys of the picture equal to null
                    profilePicture.setAd(null);
                    profilePicture.setUser(null);
                    pictureRepository.save(profilePicture);

                    // Remove this picture from user
                    user.get().setPicture(null);

                    // Delete the PICTURE
                    pictureRepository.delete(profilePicture);

                    // Update the user //todo or take this first
                    userRepository.save(user.get());

                    return new Response("Slettet bildet", HttpStatus.OK);
                }
            }
            // If we get here, pictures are equal to null
            return new Response("Bildet ble ikke funnet i databasen", HttpStatus.NOT_FOUND);
        }
        return new Response("Annonsen med spesifisert ID ikke funnet", HttpStatus.NOT_FOUND);
    }

    @Override
    public Response updatePicture(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.getById(userId);
        System.out.println(file.getName());
        System.out.println(file.getContentType());
//        String filename = file.getName().split("\\.")[1];
//        if (file.isEmpty() || !filename.equalsIgnoreCase("jpg") || !filename.equalsIgnoreCase("png") || !filename.equalsIgnoreCase("jpeg") ){
//            return new Response("File type is not correct", HttpStatus.NOT_ACCEPTABLE);
//        }
        Picture picture = Picture.builder()
                .filename(file.getName())
                .type(file.getContentType())
                .data(file.getBytes())
                .build();
        user.setPicture(picture);
        picture.setUser(user);
        pictureRepository.deleteByUser(user);
        userRepository.save(user);
        pictureRepository.save(picture);
        return new Response("Bildet er lagret", HttpStatus.OK);
    }

    @Override
    public PictureReturnDto getPicture(Long userId) {
        User user = userRepository.getById(userId);
        List<Picture> picture = pictureRepository.findByUser(user);
        return PictureReturnDto.builder()
                .base64(Base64.getEncoder().encodeToString(picture.get(0).getData()))
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
    public Response updateUser(Long userId, UserUpdateDto userUpdateDto) {
        Optional<User> userFromDB = userRepository.findById(userId);
        if(userFromDB.isEmpty()){
            return new Response("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userFromDB.get();
        if (userUpdateDto.getFirstName() != null) {
            user.setFirstName(userUpdateDto.getFirstName());
        }
        if (userUpdateDto.getLastName() != null) {
            user.setLastName(userUpdateDto.getLastName());
        }
        if (userUpdateDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }
        System.out.println(user.getFirstName() + " " + user.getEmail());
        userRepository.save(user);
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
            return new Response("User not found", HttpStatus.NOT_FOUND);
        }
        User userGot = userFromDB.get();
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
