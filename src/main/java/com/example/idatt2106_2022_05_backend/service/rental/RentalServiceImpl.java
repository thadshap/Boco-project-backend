package com.example.idatt2106_2022_05_backend.service.rental;

import com.example.idatt2106_2022_05_backend.dto.PictureReturnDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalListDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalReviewDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalUpdateDto;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.email.EmailService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

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

    @Autowired
    private PictureRepository pictureRepository;

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
        if (rentalDto.getBorrower().equals(rentalDto.getOwner())) {
            return new Response("Cannot borrow your own Ad", HttpStatus.NOT_ACCEPTABLE);
        }
        Optional<Ad> ad = adRepository.findById(rentalDto.getAdId());
        if (ad.isPresent()) {
            rentalDto.setActive(false);
            User owner = userRepository.getByEmail(rentalDto.getOwner());
            User borrower = userRepository.getByEmail(rentalDto.getBorrower());
            Rental rental = Rental.builder().borrower(borrower).owner(owner).ad(ad.get())
                    .dateOfRental(rentalDto.getDateOfRental()).rentFrom(rentalDto.getRentFrom())
                    .rentTo(rentalDto.getRentTo()).deadline(rentalDto.getDeadline()).active(rentalDto.isActive())
                    .price(rentalDto.getPrice()).build();
            borrower.getRentalsBorrowed().add(rental);
            owner.getRentalsOwned().add(rental);
            ad.get().getRentals().add(rental);
            userRepository.save(borrower);
            userRepository.save(owner);
            adRepository.save(ad.get());
            rentalRepository.save(rental);
            return new Response("Rental object is now created", HttpStatus.OK);
        } else {
            return new Response("Owner and/or borrower not present", HttpStatus.NOT_FOUND);
        }
    }
    // Ad ad = adRepository.getById(rentalDto.getAdId());

    // for (CalendarDate calDate : cld) {
    // if(!(calDate.getDate().isBefore(rentalDto.getRentTo()) && calDate.getDate().isAfter(rentalDto.getRentFrom())
    // && calDate.isAvailable())){
    // return new Response("Rental is not available in those dates", HttpStatus.NOT_FOUND);
    // }
    // }

    /**
     * Method to activate rental objec for user.
     * @param rentalId id of rental.
     * @return returns response and status.
     */
    @Override
    public Response activateRental(Long rentalId) {
        Optional<Rental> rentalFound = rentalRepository.findById(rentalId);
        if (rentalFound.isEmpty()){
            return new Response("Rental is not found in the database", HttpStatus.NO_CONTENT);
        }
        else {
            Rental rental = rentalFound.get();

            // Find the ad
            Ad ad = rental.getAd();

            // Get all dates for the ad
            Set<CalendarDate> cld = ad.getDates();

            // todo @mæsjøl hvis testan feile så sett active til false hihi
            // Set the selected dates to active
            for (CalendarDate calDate : cld) { //todo also check for equal to start + end date

                // Get the LocalDate
                LocalDate date = calDate.getDate();
                // If date equals start -or end date OR date is in between start -and endDate
                if (date.isEqual(rental.getRentFrom()) || date.isEqual(rental.getRentTo()) ||
                        (date.isBefore(rental.getRentTo()) && date.isAfter(rental.getRentFrom()))) {

                    // Mark the date as unavailable
                    calDate.setAvailable(false);

                    // Persist the change
                    dayDateRepository.save(calDate);
                }
            }
            // Set the rental as active
            rental.setActive(true);

            // Persist the rental
            rentalRepository.save(rental);

            // emailService.sendEmail("BOCO", rental.getBorrower().getEmail(), "Utån Godkjent!",
            // "Ditt låneforespørsel av " + rental.getAd().getTitle() + ", er nå godkjent av utleier!");
            return new Response("Rental has been activated", HttpStatus.ACCEPTED);
        }
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
    public Response completeRental(Long rentalId, RentalReviewDto rating) {
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);
        if (rentalOptional.isEmpty()){
            return new Response("Rental is not found in the database", HttpStatus.NO_CONTENT);
        }
        Rental rental = rentalOptional.get();
        rental.setRating(rating.getRating());
        rental.setActive(false);
        // User user = userRepository.getById(rental.getOwner().getId());
        Optional<User> userFound = userRepository.findById(rental.getOwner().getId());
        if (userFound.isPresent()) {
            User user = userFound.get();
            user.setNumberOfReviews(user.getNumberOfReviews() + 1);
            user.setRating((user.getRating() + rating.getRating()) / user.getNumberOfReviews());
            Review review = Review.builder().user(user).description(rating.getReview()).rating((int) rating.getRating())
                    .ad(rental.getAd()).build();
            user.getReviews().add(review);
            Ad ad = rental.getAd();
            ad.getReviews().add(review);
            adRepository.save(ad);
            userRepository.save(user);
            rentalRepository.save(rental);
            return new Response("Rental has been deactivated", HttpStatus.ACCEPTED);
        } else {
            return new Response("Could not find user", HttpStatus.NOT_FOUND);
        }

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
            return new Response("Rental not found!", HttpStatus.NO_CONTENT);
        }

        Rental rental = rentalOptional.get();

        if (rentalDto.getRentFrom() != null) {
            rental.setRentFrom(rentalDto.getRentFrom());
        }
        if (rentalDto.getRentTo() != null) {
            rental.setRentTo(rentalDto.getRentTo());
        }
        if (rentalDto.getDeadline() != null) {
            rental.setDeadline(rentalDto.getDeadline());
        }
        if (rentalDto.getPrice() >= 0) {
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
            return new Response("Rental not found!", HttpStatus.NO_CONTENT);
        }
        System.out.println("returning the rental");
        Rental rental = rentalOptional.get();
        RentalDto rentalReturn = RentalDto.builder()
                .id(rental.getId())
                .adId(rental.getAd().getId())
                .title(rental.getAd().getTitle())
                .borrower(rental.getBorrower().getFirstName() + " " + rental.getBorrower().getLastName())
                .owner(rental.getOwner().getFirstName() + " " + rental.getOwner().getLastName())
                .active(rental.isActive())
                .dateOfRental(rental.getDateOfRental())
                .deadline(rental.getDeadline())
                .rentFrom(rental.getRentFrom())
                .rentTo(rental.getRentTo())
                .price(rental.getPrice())
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
            return new Response("Rentals not found!", HttpStatus.NO_CONTENT);
        }
        for (int i = 0; i < rental.size(); i++) {
            RentalDto rentalReturn = RentalDto.builder()
                    .id(rental.get(i).getId())
                    .adId(rental.get(i).getAd().getId())
                    .title(rental.get(i).getAd().getTitle())
                    .borrower(rental.get(i).getBorrower().getFirstName() + " " + rental.get(i).getBorrower().getLastName())
                    .owner(rental.get(i).getOwner().getFirstName() + " " + rental.get(i).getOwner().getLastName())
                    .active(rental.get(i).isActive())
                    .dateOfRental(rental.get(i).getDateOfRental())
                    .deadline(rental.get(i).getDeadline())
                    .rentFrom(rental.get(i).getRentFrom())
                    .rentTo(rental.get(i).getRentTo())
                    .price(rental.get(i).getPrice())
                    .build();
            rentals.getRentals().add(rentalReturn);
        }
        return new Response(rentals, HttpStatus.OK);
    }

    /**
     * Method to retrieve first picture from Ad of rental object.
     * @param rentalId id of rental.
     * @return returns response and status.
     */
    @Override
    public Response getRentalPictureById(Long rentalId) {
        Rental rental = rentalRepository.getById(rentalId);
        List<Picture> pictures = pictureRepository.findByAd(rental.getAd());
        PictureReturnDto returnDto = PictureReturnDto.builder()
                .base64(Base64.getEncoder().encodeToString(pictures.get(0).getData()))
                .type(pictures.get(0).getType())
                .build();
        returnDto.setId(rentalId);
        return new Response(returnDto, HttpStatus.OK);
    }
}
