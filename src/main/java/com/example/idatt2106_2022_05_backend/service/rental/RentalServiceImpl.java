package com.example.idatt2106_2022_05_backend.service.rental;

import com.example.idatt2106_2022_05_backend.dto.RentalDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.CalendarDate;
import com.example.idatt2106_2022_05_backend.model.Rental;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CalendarDateRepository;
import com.example.idatt2106_2022_05_backend.repository.RentalRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Autowired
    private CalendarDateRepository dayDateRepository;

    private ModelMapper modelMapper = new ModelMapper();

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
        if (rentalDto.getBorrower().equals(rentalDto.getOwner())){
            return new Response("Cannot borrow your own Ad", HttpStatus.NOT_ACCEPTABLE);
        }
        Ad ad = adRepository.getById(rentalDto.getAd());
        Set<CalendarDate> cld = ad.getDates();
        for (CalendarDate calDate : cld) {
            if(!(calDate.getDate().isBefore(rentalDto.getRentTo()) && calDate.getDate().isAfter(rentalDto.getRentFrom())
                    && calDate.isAvailable())){
                return new Response("Rental is not available in those dates", HttpStatus.NOT_FOUND);
            }
        }
        for (CalendarDate calDate: cld) {
            if(calDate.getDate().isBefore(rentalDto.getRentTo()) && calDate.getDate().isAfter(rentalDto.getRentFrom())){
                calDate.setAvailable(false);
                dayDateRepository.save(calDate);
            }
        }
        User owner = userRepository.getById(rentalDto.getOwner());
        User borrower = userRepository.getById(rentalDto.getBorrower());
        Rental rental = modelMapper.map(rentalDto, Rental.class);
        rental.setBorrower(borrower);
        rental.setOwner(owner);
        rental.setAd(ad);
        borrower.getRentalsBorrowed().add(rental);
        owner.getRentalsOwned().add(rental);
        ad.getRentals().add(rental);
        userRepository.save(borrower);
        userRepository.save(owner);
        adRepository.save(ad);
        rentalRepository.save(rental);
        return new Response("Rental object is now created", HttpStatus.OK);
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
        Rental rental = rentalRepository.getById(rentalId);
        if (rental == null){
            return new Response("Rental is not found in the database", HttpStatus.NOT_FOUND);
        }
        rentalRepository.deleteById(rental.getId());
        return new Response("Rental has been deleted", HttpStatus.ACCEPTED);
    }

    /**
     * Method to update Rental object
     * 
     * @param rentalDto
     *            {@link RentalDto} object with information to update a rental
     *
     * @param rentalId
     *            Id of the rental to delete.
     *
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response updateRental(RentalDto rentalDto, Long rentalId) {
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);

        if (rentalOptional.isEmpty()){
            return new Response("Rental not found!", HttpStatus.NOT_FOUND);
        }
        Rental rental = rentalOptional.get();
        if (rentalDto.getRentTo() != null){
            rental.setRentTo(rentalDto.getRentTo());
        }
        if (rentalDto.getDeadline() != null){
            rental.setDeadline(rentalDto.getDeadline());
        }
        return new Response("Rental", HttpStatus.ACCEPTED);
    }

    /**
     * Method to retrieve a Rental Object
     *
     * @param rentalId
     *            Id of the rental to delete.
     *
     * @return returns HttpStatus and a response object with.
     */
    @Override
    public Response getRental(Long rentalId) {
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);

        if (rentalOptional.isEmpty()){
            return new Response("Rental not found!", HttpStatus.NOT_FOUND);
        }
        Rental rental = rentalOptional.get();
        return new Response(rental, HttpStatus.OK);
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
        User user = userRepository.getById(userId);
        List<Rental> rental = rentalRepository.getByOwner(user);
        rental.addAll(rentalRepository.getByBorrower(user));

        if (rental.isEmpty()){
            return new Response("Rentals not found!", HttpStatus.NOT_FOUND);
        }
        return new Response(rental, HttpStatus.OK);
    }
}
