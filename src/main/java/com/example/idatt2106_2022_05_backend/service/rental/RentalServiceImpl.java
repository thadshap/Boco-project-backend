package com.example.idatt2106_2022_05_backend.service.rental;

import com.example.idatt2106_2022_05_backend.dto.rental.RentalDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalListDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalReviewDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalUpdateDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.CalendarDate;
import com.example.idatt2106_2022_05_backend.model.Rental;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CalendarDateRepository;
import com.example.idatt2106_2022_05_backend.repository.RentalRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.service.email.EmailService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
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

    @Autowired
    private EmailService emailService;

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
        Optional<Ad> ad = adRepository.findById(rentalDto.getAdId());
        if(ad.isPresent()) {
            Set<CalendarDate> cld = ad.get().getDates();

            if (rentalDto.isActive()){
                for (CalendarDate calDate: cld) {
                    if(calDate.getDate().isBefore(rentalDto.getRentTo()) && calDate.getDate().isAfter(rentalDto.getRentFrom())){
                        calDate.setAvailable(false);
                        dayDateRepository.save(calDate);
                    }
                }
            }
            rentalDto.setActive(false);
            // User owner = userRepository.getById(rentalDto.getOwner());
            // User borrower = userRepository.getById(rentalDto.getBorrower());
            Optional<User> owner = userRepository.findById(rentalDto.getOwner());
            Optional<User> borrower = userRepository.findById(rentalDto.getBorrower());
            if(owner.isPresent() && borrower.isPresent()) {
                Rental rental = Rental.builder()
                        .borrower(borrower.get())
                        .owner(owner.get())
                        .ad(ad.get())
                        .dateOfRental(rentalDto.getDateOfRental())
                        .rentFrom(rentalDto.getRentFrom())
                        .rentTo(rentalDto.getRentTo())
                        .deadline(rentalDto.getDeadline())
                        .active(rentalDto.isActive())
                        .price(rentalDto.getPrice())
                        .build();
                borrower.get().getRentalsBorrowed().add(rental);
                owner.get().getRentalsOwned().add(rental);
                ad.get().getRentals().add(rental);
                userRepository.save(borrower.get());
                userRepository.save(owner.get());
                adRepository.save(ad.get());
                rentalRepository.save(rental);
                return new Response("Rental object is now created", HttpStatus.OK);
            }
            else {
                return new Response("Owner and/or borrower not present", HttpStatus.NOT_FOUND);
            }
        }

        else {
            return new Response("Rental is not available in those dates", HttpStatus.NOT_FOUND);
        }
        //Ad ad = adRepository.getById(rentalDto.getAdId());

//        for (CalendarDate calDate : cld) {
//            if(!(calDate.getDate().isBefore(rentalDto.getRentTo()) && calDate.getDate().isAfter(rentalDto.getRentFrom())
//                    && calDate.isAvailable())){
//                return new Response("Rental is not available in those dates", HttpStatus.NOT_FOUND);
//            }
//        }
    }

    @Override
    public Response activateRental(Long rentalId) throws MessagingException {
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);
        if (rentalOptional.isEmpty()){
            return new Response("Rental is not found in the database", HttpStatus.NOT_FOUND);
        }
        Rental rental = rentalOptional.get();
        rental.setActive(true);
        Set<CalendarDate> cld = rental.getAd().getDates();
        for (CalendarDate calDate: cld) {
            if(calDate.getDate().isBefore(rental.getRentTo()) &&
                    calDate.getDate().isAfter(rental.getRentFrom())){
                calDate.setAvailable(false);
                dayDateRepository.save(calDate);
            }
        }
        rentalRepository.save(rental);
        //TODO check if right user gets confirm email
        emailService.sendEmail("BOCO", rental.getBorrower().getEmail(), "Utån Godkjent!",
                "Ditt låneforespørsel av " + rental.getAd().getTitle() + ", er nå godkjent av utleier!");
        return new Response("Rental has been activated", HttpStatus.ACCEPTED);
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
    public Response deleteRental(Long rentalId, RentalReviewDto rating) {
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);
        if (rentalOptional.isEmpty()){
            return new Response("Rental is not found in the database", HttpStatus.NOT_FOUND);
        }
        Rental rental = rentalOptional.get();
        rental.setRating(rating.getRating());
        rental.setActive(false);
        rentalRepository.save(rental);
        return new Response("Rental has been deactivated", HttpStatus.ACCEPTED);
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
    public Response updateRental(RentalUpdateDto rentalDto, Long rentalId) {
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);

        if (rentalOptional.isEmpty()){
            return new Response("Rental not found!", HttpStatus.NOT_FOUND);
        }

        Rental rental = rentalOptional.get();

        if (rentalDto.getRentFrom() != null){
            rental.setRentFrom(rentalDto.getRentFrom());
        }
        if (rentalDto.getRentTo() != null){
            rental.setRentTo(rentalDto.getRentTo());
        }
        if (rentalDto.getDeadline() != null){
            rental.setDeadline(rentalDto.getDeadline());
        }
        if (rentalDto.getPrice() >= 0){
            rental.setPrice(rentalDto.getPrice());
        }

        // Persist the changes
        rentalRepository.save(rental);

        return new Response("Rental updated", HttpStatus.ACCEPTED);
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
        System.out.println("rental id = " + rentalId);
        if (rentalOptional.isEmpty()){
            return new Response("Rental not found!", HttpStatus.NOT_FOUND);
        }
        System.out.println("returning the rental");
        Rental rental = rentalOptional.get();
        RentalDto rentalReturn = RentalDto.builder()
                .adId(rental.getAd().getId())
                .borrower(rental.getBorrower().getId())
                .owner(rental.getOwner().getId())
                .active(rental.isActive())
                .dateOfRental(rental.getDateOfRental())
                .deadline(rental.getDeadline())
                .rentFrom(rental.getRentFrom())
                .rentTo(rental.getRentTo())
                .build();
        return new Response(rentalReturn, HttpStatus.OK);
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
        RentalListDto rentals = new RentalListDto();
        rentals.setRentals(new ArrayList<>());
        List<Rental> rental = rentalRepository.getByOwner(user);
        rental.addAll(rentalRepository.getByBorrower(user));

        if (rental.isEmpty()){
            return new Response("Rentals not found!", HttpStatus.NOT_FOUND);
        }
        for (int i = 0; i < rental.size(); i++) {
            RentalDto rentalReturn = RentalDto.builder()
                    .id(rental.get(i).getId())
                    .adId(rental.get(i).getAd().getId())
                    .borrower(rental.get(i).getBorrower().getId())
                    .owner(rental.get(i).getOwner().getId())
                    .active(rental.get(i).isActive())
                    .dateOfRental(rental.get(i).getDateOfRental())
                    .deadline(rental.get(i).getDeadline())
                    .rentFrom(rental.get(i).getRentFrom())
                    .rentTo(rental.get(i).getRentTo())
                    .build();
            rentals.getRentals().add(rentalReturn);
        }
        return new Response(rentals, HttpStatus.OK);
    }
}
