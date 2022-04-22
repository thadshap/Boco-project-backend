package com.example.idatt2106_2022_05_backend.service.rental;

import com.example.idatt2106_2022_05_backend.dto.RentalDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Rental;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.RentalRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service Rental class to handle rental objects
 */
@Service
public class RentalServiceImpl implements RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    /**
     * Method to create Rental object
     * 
     * @param rentalDto
     *            {@link RentalDto} object with information to create a rental
     * 
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response createRental(RentalDto rentalDto) {
        Ad ad = adRepository.getById(rentalDto.getAd());
        List<Rental> rentals = rentalRepository.findByAd(ad);
        return null;
    }

    /**
     * Method to delete a rental object from the repository.
     * 
     * @param rentalId
     *            Id of the rental to delete.
     * 
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response deleteRental(Long rentalId) {

        return null;
    }

    /**
     * Method to update Rental object
     * 
     * @param rentalDto
     *            {@link RentalDto} object with information to update a rental
     * 
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response updateRental(RentalDto rentalDto) {

        return null;
    }

    /**
     * Method to retrieve a Rental Object
     * 
     * @param rentalDto
     *            TODO change param
     * 
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response getRental(RentalDto rentalDto) {

        return null;
    }

    /**
     * Method to retrieve a Rental object by User id
     * 
     * @param userId
     *            user id to retrieve the rental object for
     * 
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response getRentalByUserId(Long userId) {

        return null;
    }

    /**
     * Method to retrieve Rental objects by User id
     * 
     * @param userId
     *            user id to retrieve the rental object for
     * 
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response getRentalsByUserId(Long userId) {

        return null;
    }
}
