package com.example.idatt2106_2022_05_backend.service.user;

import com.example.idatt2106_2022_05_backend.dto.user.UserReturnDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserUpdateDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.model.Rental;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.PictureRepository;
import com.example.idatt2106_2022_05_backend.repository.RentalRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.PictureUtility;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private RentalRepository rentalRepository;

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
            // Get the ads
            Set<Ad> ads = userFound.get().getAds();
            if(ads != null) { //todo set user == null if this does not work
                // Iterate over all the ads and delete them
                for(Ad ad : ads) {
                    adService.deleteAd(ad.getId());
                }
            }
        }
        // Get the rentals
        List<Rental> rentals = userFound.get().getRentalsOwned();
        if(rentals != null) {
            for(Rental rental : rentals) {
                rental.setOwner(null);
                rentalRepository.save(rental);
            }
        }

        // Delete rentals from user
        userFound.get().setRentalsOwned(null);

        List<Rental> rentals2 = userFound.get().getRentalsBorrowed();
        if(rentals != null) {
            for(Rental rental : rentals2) {
                rental.setBorrower(null);
                rentalRepository.save(rental);
            }
        }
        userRepository.deleteById(userId);

        // Delete rentals from user
        userFound.get().setRentalsBorrowed(null);

        return new Response("User deleted", HttpStatus.ACCEPTED);
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
        if (userUpdateDto.getEmail() != null) {
            user.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }
        if(userUpdateDto.getPicture() != null) {
            pictureService.savePicture(userUpdateDto.getPicture(),0,userId);
        }
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
        UserReturnDto user = modelMapper.map(userFromDB.get(), UserReturnDto.class);
        return new Response(user, HttpStatus.OK);
    }

}
