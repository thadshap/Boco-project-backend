package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.CalendarDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalReviewDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalUpdateDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.Rental;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.service.calendar.CalendarService;
import com.example.idatt2106_2022_05_backend.service.rental.RentalService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RentalIntegrationTest {

    @Autowired
    AdService adService;
    @Autowired
    AdRepository adRepository;

    @Autowired
    RentalRepository rentalRepository;

    @Autowired
    RentalService rentalService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CalendarDateRepository calendarDateRepository;

    @Autowired
    CalendarService calendarService;

    @Autowired
    ReviewRepository reviewRepository;

    @SneakyThrows
    @Test
    public void rentalSaved_WhenForeignKeysCorrect() {
        // Building a user
        User owner1 = User.builder().firstName("firstName").lastName("lastName").email("user1.name@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User owner = userRepository.save(owner1);

        // Create a category
        Category it = Category.builder().name("new category2").parent(true).build();

        // Saving the categories
        categoryRepository.save(it);

        // Building a user
        User borrower1 = User.builder().firstName("firstName").lastName("lastName").email("user2.otherName@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User borrower = userRepository.save(borrower1);

        // Create an ad
        // Create ads as well
        AdDto speaker1 = AdDto.builder().title("Title that does not exist elsewhere")
                .description("Renting out a brand new speaker").rental(true).durationType(AdType.WEEK).duration(2)
                .price(100).streetAddress("vollabakken 3").postalCode(7030).city("Trondheim").userId(owner.getId())
                .categoryId(it.getId()).build();

        // persist ad
        adService.postNewAd(speaker1);

        Set<Ad> foundAds = adRepository.findByTitle("Title that does not exist elsewhere");

        Optional<Ad> foundAd = foundAds.stream().findFirst();
        assertNotNull(foundAd);

        // TODO parts of this is not implemented in CalendarDate service (deadline and active)
        RentalDto rental = RentalDto.builder().dateOfRental(LocalDate.now()).rentTo(LocalDate.now().plusWeeks(1))
                .rentFrom(LocalDate.now().plusDays(1)).deadline(LocalDate.now().plusDays(2))
                .borrower(borrower.getEmail()).owner(owner.getEmail()).adId(foundAd.get().getId()).active(true)
                .price(100).build();

        // Get previous number of rentals
        int previousNumberOfRentals = rentalRepository.findAll().size();

        // Persist rental
        rentalService.createRental(rental);

        // Get new number of rentals
        int newNumberOfRentals = rentalRepository.findAll().size();

        assertNotEquals(previousNumberOfRentals, newNumberOfRentals);
    }

    @SneakyThrows
    @Test
    public void rentalNotSaved_WhenForeignKeysWrong() {
        // Building a user
        User owner1 = User.builder().firstName("firstName").lastName("lastName").email("user3.name@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User owner = userRepository.save(owner1);

        // Create a category
        Category it = Category.builder().name("new category2").parent(true).build();

        // Saving the categories
        categoryRepository.save(it);

        // Building a user
        User borrower1 = User.builder().firstName("firstName").lastName("lastName").email("user4.otherName@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User borrower = userRepository.save(borrower1);

        // Create an ad
        // Create ads as well
        AdDto speaker1 = AdDto.builder().title("Title that does not exist elsewhere2")
                .description("Renting out a brand new speaker").rental(true).durationType(AdType.WEEK).duration(2)
                .price(100).streetAddress("vollabakken 3").postalCode(7030).city("Trondheim").userId(owner.getId())
                .categoryId(it.getId()).build();

        // persist ad
        adService.postNewAd(speaker1);

        Set<Ad> foundAds = adRepository.findByTitle("Title that does not exist elsewhere2");

        Optional<Ad> foundAd = foundAds.stream().findFirst();
        assertNotNull(foundAd);

        // Creating rental with a user that does not exist
        RentalDto rental = RentalDto.builder().dateOfRental(LocalDate.now()).rentTo(LocalDate.now().plusWeeks(1))
                .rentFrom(LocalDate.now().plusDays(1)).deadline(LocalDate.now().plusDays(2))
                .borrower(borrower.getEmail()).owner("random.email.com").adId(foundAd.get().getId()).active(true)
                .price(100).build();

        // Get previous number of rentals
        int previousNumberOfRentals = rentalRepository.findAll().size();

        // Persist rental
        try {
            ResponseEntity<Object> res = rentalService.createRental(rental);
        } catch (NullPointerException e) {
            // Passed
            // Get new number of rentals
            int newNumberOfRentals = rentalRepository.findAll().size();

            assertEquals(previousNumberOfRentals, newNumberOfRentals);
        }
    }

    @SneakyThrows
    @Test
    public void rentalCanBeCancelled_Before24Hrs() {
        // Building a user
        User owner1 = User.builder().firstName("firstName").lastName("lastName").email("user5.name@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User owner = userRepository.save(owner1);

        // Create a category
        Category it = Category.builder().name("new category2").parent(true).build();

        // Saving the categories
        categoryRepository.save(it);

        // Building a user
        User borrower1 = User.builder().firstName("firstName").lastName("lastName").email("user6.otherName@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User borrower = userRepository.save(borrower1);

        // Create an ad
        // Create ads as well
        AdDto speaker1 = AdDto.builder().title("Title that does not exist elsewhere")
                .description("Renting out a brand new speaker").rental(true).durationType(AdType.WEEK).duration(2)
                .price(100).streetAddress("vollabakken 3").postalCode(7030).city("Trondheim").userId(owner.getId())
                .categoryId(it.getId()).build();

        // persist ad
        adService.postNewAd(speaker1);

        Set<Ad> foundAds = adRepository.findByTitle("Title that does not exist elsewhere");

        Optional<Ad> foundAd = foundAds.stream().findFirst();
        assertNotNull(foundAd);

        // TODO parts of this is not implemented in CalendarDate service (deadline and active)
        RentalDto rental = RentalDto.builder().dateOfRental(LocalDate.now()).rentTo(LocalDate.now().plusWeeks(1))
                .rentFrom(LocalDate.now().plusDays(1)).deadline(LocalDate.now().plusDays(2))
                .borrower(borrower.getEmail()).owner(owner.getEmail()).adId(foundAd.get().getId()).active(true)
                .price(100).build();

        // Get previous number of rentals
        int previousNumberOfRentals = rentalRepository.findAll().size();

        // Persist rental
        rentalService.createRental(rental);

        Optional<Rental> rentalFound = null;

        List<Rental> rentals = rentalRepository.getByBorrower(borrower);
        if (rentals != null) {
            if (rentals.size() > 1) {
                rentalFound = Optional.ofNullable(rentals.get(-1));
            }
            rentalFound = rentals.stream().findFirst();
        }

        assertNotNull(rentalFound);

        // Get new number of rentals
        int newNumberOfRentals = rentalRepository.findAll().size();

        assertNotEquals(previousNumberOfRentals, newNumberOfRentals);

        // Create a dto object to mark dates as unavailable
        CalendarDto dtoMock = CalendarDto.builder().available(true).startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusWeeks(1)).adId(foundAd.get().getId()).rentalId(rentalFound.get().getId())
                .build();

        // Try to cancel the rental
        ResponseEntity<Object> response = calendarService.markDatesFromToAs(dtoMock);

        // Assert HttpResponse = OK
        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());

        // Assert that all dates are available now
        ResponseEntity<Object> response2 = calendarService.getUnavailableDates(dtoMock);
        assertEquals(response2.getStatusCodeValue(), HttpStatus.OK.value());
    }

    @SneakyThrows
    @Test
    public void rentalCanNotBeCancelled_After24Hrs() {
        // Building a user
        User owner1 = User.builder().firstName("firstName").lastName("lastName").email("user7.name@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User owner = userRepository.save(owner1);

        // Create a category
        Category it = Category.builder().name("new category2").parent(true).build();

        // Saving the categories
        categoryRepository.save(it);

        // Building a user
        User borrower1 = User.builder().firstName("firstName").lastName("lastName").email("user8.otherName@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User borrower = userRepository.save(borrower1);

        // Create an ad
        // Create ads as well
        AdDto speaker1 = AdDto.builder().title("Title that does not exist elsewhere")
                .description("Renting out a brand new speaker").rental(true).durationType(AdType.WEEK).duration(2)
                .price(100).streetAddress("vollabakken 3").postalCode(7030).city("Trondheim").userId(owner.getId())
                .categoryId(it.getId()).build();

        // persist ad
        adService.postNewAd(speaker1);

        Set<Ad> foundAds = adRepository.findByTitle("Title that does not exist elsewhere");

        Optional<Ad> foundAd = foundAds.stream().findFirst();
        assertNotNull(foundAd);

        // TODO parts of this is not implemented in CalendarDate service (deadline and active)
        Rental rental = Rental.builder().dateOfRental(LocalDate.now()).rentFrom(LocalDate.now().minusDays(2))
                .rentTo(LocalDate.now().plusWeeks(1)).deadline(LocalDate.now().minusDays(1)).borrower(borrower)
                .owner(owner).ad(foundAd.get()).active(true).price(100).build();

        // Get previous number of rentals
        int previousNumberOfRentals = rentalRepository.findAll().size();

        // Persist rental
        Rental rentalFound = rentalRepository.save(rental);

        // Set created to some arbitrary date that makes LocalDateTime.now() way too late for cancellation
        rentalFound.setCreated(LocalDateTime.now().minusWeeks(1).minusDays(2));
        rentalRepository.save(rentalFound);
        System.out.println(rentalFound.getCreated());

        assertNotNull(rentalFound);

        // Get new number of rentals
        int newNumberOfRentals = rentalRepository.findAll().size();

        assertNotEquals(previousNumberOfRentals, newNumberOfRentals);

        // Create a dto object to mark dates as unavailable
        CalendarDto dtoMock = CalendarDto.builder().available(true).startDate(LocalDate.now().minusDays(2))
                .endDate(LocalDate.now().plusWeeks(1)).adId(foundAd.get().getId()).rentalId(rentalFound.getId())
                .build();

        // Try to cancel the rental
        ResponseEntity<Object> response = calendarService.markDatesFromToAs(dtoMock);

        // Assert HttpResponse = NOT_ACCEPTABLE
        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_ACCEPTABLE.value());

        // Assert that all dates are available now
        ResponseEntity<Object> response2 = calendarService.getUnavailableDates(dtoMock);
        assertEquals(response2.getStatusCodeValue(), HttpStatus.OK.value());
    }

    @SneakyThrows
    @Test
    public void activateRental() {
        // Building a user
        User owner1 = User.builder().firstName("firstName").lastName("lastName").email("user9.name@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User owner = userRepository.save(owner1);

        // Create a category
        Category it = Category.builder().name("new category2").parent(true).build();

        // Saving the categories
        categoryRepository.save(it);

        // Building a user
        User borrower1 = User.builder().firstName("firstName").lastName("lastName")
                .email("user10.otherName@hotmail.com").password("pass1word").build();

        // Saving the user
        User borrower = userRepository.save(borrower1);

        // Create an ad
        // Create ads as well
        AdDto speaker1 = AdDto.builder().title("Title that does not exist elsewhere")
                .description("Renting out a brand new speaker").rental(true).durationType(AdType.WEEK).duration(2)
                .price(100).streetAddress("vollabakken 3").postalCode(7030).city("Trondheim").userId(owner.getId())
                .categoryId(it.getId()).build();

        // persist ad
        adService.postNewAd(speaker1);

        Set<Ad> foundAds = adRepository.findByTitle("Title that does not exist elsewhere");

        Optional<Ad> foundAd = foundAds.stream().findFirst();
        assertNotNull(foundAd);

        // Create rental (not active)
        Rental rental = Rental.builder().dateOfRental(LocalDate.now()).rentFrom(LocalDate.now().minusDays(2))
                .rentTo(LocalDate.now().plusWeeks(1)).deadline(LocalDate.now().minusDays(1)).borrower(borrower)
                .owner(owner).ad(foundAd.get()).active(false).price(100).build();

        // Persist rental
        Rental rentalFound = rentalRepository.save(rental);

        // Activate rental
//        ResponseEntity<Object> response = rentalService.activateRental(rentalFound.getId());
//        assertEquals(HttpStatus.ACCEPTED.value(), response.getStatusCodeValue());
        assertNotEquals(rentalFound.isActive(), rentalRepository.findById(rentalFound.getId()).get().isActive());
    }

    @SneakyThrows
    @Test
    public void rentalDeleted() {
        // Building a user
        User owner1 = User.builder().firstName("firstName").lastName("lastName").email("user11.name@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User owner = userRepository.save(owner1);

        // Create a category
        Category it = Category.builder().name("new category2").parent(true).build();

        // Saving the categories
        categoryRepository.save(it);

        // Building a user
        User borrower1 = User.builder().firstName("firstName").lastName("lastName")
                .email("user12.otherName@hotmail.com").password("pass1word").build();

        // Saving the user
        User borrower = userRepository.save(borrower1);

        // Create an ad
        // Create ads as well
        AdDto speaker1 = AdDto.builder().title("Title that does not exist elsewhere")
                .description("Renting out a brand new speaker").rental(true).durationType(AdType.WEEK).duration(2)
                .price(100).streetAddress("vollabakken 3").postalCode(7030).city("Trondheim").userId(owner.getId())
                .categoryId(it.getId()).build();

        // persist ad
        adService.postNewAd(speaker1);

        Set<Ad> foundAds = adRepository.findByTitle("Title that does not exist elsewhere");

        Optional<Ad> foundAd = foundAds.stream().findFirst();
        assertNotNull(foundAd);

        // Create rental (not active)
        Rental rental = Rental.builder().dateOfRental(LocalDate.now()).rentFrom(LocalDate.now().minusDays(2))
                .rentTo(LocalDate.now().plusWeeks(1)).deadline(LocalDate.now().minusDays(1)).borrower(borrower)
                .owner(owner).ad(foundAd.get()).active(true).price(100).build();

        // Persist rental
        Rental rentalFound = rentalRepository.save(rental);

        // Create a rental-review DTO in order to execute deletion
        RentalReviewDto dto = RentalReviewDto.builder().rating(5).build();

        // Delete rental
        ResponseEntity<Object> response = rentalService.completeRental(rentalFound.getId(), dto);
        // Assert accepted http response
        assertEquals(HttpStatus.ACCEPTED.value(), response.getStatusCodeValue());
        // Assert that the rental now is not active
        assertNotEquals(rentalFound.isActive(), rentalRepository.findById(rentalFound.getId()).get().isActive());
    }

    // Update rental
    @SneakyThrows
    @Test
    public void rentalIsUpdated_WhenArgsAreCorrect() {
        // Building a user
        User owner1 = User.builder().firstName("firstName").lastName("lastName").email("user13.name@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User owner = userRepository.save(owner1);

        // Create a category
        Category it = Category.builder().name("new category2").parent(true).build();

        // Saving the categories
        categoryRepository.save(it);

        // Building a user
        User borrower1 = User.builder().firstName("firstName").lastName("lastName")
                .email("user14.otherName@hotmail.com").password("pass1word").build();

        // Saving the user
        User borrower = userRepository.save(borrower1);

        // Create an ad
        // Create ads as well
        AdDto speaker1 = AdDto.builder().title("Title that does not exist elsewhere2")
                .description("Renting out a brand new speaker").rental(true).durationType(AdType.WEEK).duration(2)
                .price(100).streetAddress("vollabakken 3").postalCode(7030).city("Trondheim").userId(owner.getId())
                .categoryId(it.getId()).build();

        // persist ad
        adService.postNewAd(speaker1);

        Set<Ad> foundAds = adRepository.findByTitle("Title that does not exist elsewhere2");

        Optional<Ad> foundAd = foundAds.stream().findFirst();
        assertNotNull(foundAd);

        // Create rental (not active)
        Rental rental = Rental.builder().dateOfRental(LocalDate.now()).rentFrom(LocalDate.now().minusDays(2))
                .rentTo(LocalDate.now().plusWeeks(1)).deadline(LocalDate.now().minusDays(1)).borrower(borrower)
                .owner(owner).ad(foundAd.get()).active(true).price(100).build();

        // Persist rental
        Rental rentalFound = rentalRepository.save(rental);

        // Assert not null
        assertNotNull(rentalFound);

        // Create a dto to update the rental
        RentalUpdateDto dto = RentalUpdateDto.builder().price(80).build();

        // Update the rental
        ResponseEntity<Object> res = rentalService.updateRental(dto, rentalFound.getId());

        // Retrieve the updated rental
        Optional<Rental> updatedRental = rentalRepository.findById(rentalFound.getId());
        assertNotNull(updatedRental);

        // Test that the price was updated
        assertEquals(80, updatedRental.get().getPrice());
        assertEquals(HttpStatus.ACCEPTED.value(), res.getStatusCodeValue());
    }

    @SneakyThrows
    @Test
    public void rentalIsNotUpdated_WhenArgsAreWrong() {
        // Building a user
        User owner1 = User.builder().firstName("firstName").lastName("lastName").email("user15.name@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User owner = userRepository.save(owner1);

        // Create a category
        Category it = Category.builder().name("new category2").parent(true).build();

        // Saving the categories
        categoryRepository.save(it);

        // Building a user
        User borrower1 = User.builder().firstName("firstName").lastName("lastName")
                .email("user16.otherName@hotmail.com").password("pass1word").build();

        // Saving the user
        User borrower = userRepository.save(borrower1);

        // Create an ad
        // Create ads as well
        AdDto speaker1 = AdDto.builder().title("Title that does not exist elsewhere3")
                .description("Renting out a brand new speaker").rental(true).durationType(AdType.WEEK).duration(2)
                .price(100).streetAddress("vollabakken 3").postalCode(7030).city("Trondheim").userId(owner.getId())
                .categoryId(it.getId()).build();

        // persist ad
        adService.postNewAd(speaker1);

        Set<Ad> foundAds = adRepository.findByTitle("Title that does not exist elsewhere3");

        Optional<Ad> foundAd = foundAds.stream().findFirst();
        assertNotNull(foundAd);

        // Create rental (not active)
        Rental rental = Rental.builder().dateOfRental(LocalDate.now()).rentFrom(LocalDate.now().minusDays(2))
                .rentTo(LocalDate.now().plusWeeks(1)).deadline(LocalDate.now().minusDays(1)).borrower(borrower)
                .owner(owner).ad(foundAd.get()).active(true).price(100).build();

        // Persist rental
        rentalRepository.save(rental);

        // Create a dto to update the rental
        RentalUpdateDto dto = RentalUpdateDto.builder().price(80).build();

        // Update the rental using random id
        ResponseEntity<Object> res = rentalService.updateRental(dto, 100000000L);

        // Retrieve the updated rental
        assertEquals(HttpStatus.NO_CONTENT.value(), res.getStatusCodeValue());
    }

    @SneakyThrows
    @Test
    public void getRentalById() {
        // Create 1 rental
        User owner1 = User.builder().firstName("firstName").lastName("lastName").email("user17.name@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User owner = userRepository.save(owner1);

        // Create a category
        Category it = Category.builder().name("new category2").parent(true).build();

        // Saving the categories
        categoryRepository.save(it);

        // Building a user
        User borrower1 = User.builder().firstName("firstName").lastName("lastName")
                .email("user18.otherName@hotmail.com").password("pass1word").build();

        // Saving the user
        User borrower = userRepository.save(borrower1);

        // Create an ad
        // Create ads as well
        AdDto speaker1 = AdDto.builder().title("Title that does not exist elsewhere3")
                .description("Renting out a brand new speaker").rental(true).durationType(AdType.WEEK).duration(2)
                .price(100).streetAddress("vollabakken 3").postalCode(7030).city("Trondheim").userId(owner.getId())
                .categoryId(it.getId()).build();

        // persist ad
        adService.postNewAd(speaker1);

        Set<Ad> foundAds = adRepository.findByTitle("Title that does not exist elsewhere3");

        Optional<Ad> foundAd = foundAds.stream().findFirst();
        assertNotNull(foundAd);

        // Create rental (not active)
        Rental rental = Rental.builder().dateOfRental(LocalDate.now()).rentFrom(LocalDate.now().minusDays(2))
                .rentTo(LocalDate.now().plusWeeks(1)).deadline(LocalDate.now().minusDays(1)).borrower(borrower)
                .owner(owner).ad(foundAd.get()).active(true).price(100).build();

        Rental rentalSaved = rentalRepository.save(rental);

        assertTrue(rentalRepository.findAll().size() > 0);

        ResponseEntity<Object> rentalFound = rentalService.getRental(rentalSaved.getId());
        assertEquals(HttpStatus.OK.value(), rentalFound.getStatusCodeValue());
    }

    @SneakyThrows
    @Test
    public void getRentalsByUserId() {
        // Create two rentals
        User owner1 = User.builder().firstName("firstName").lastName("lastName").email("user19.name@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        User owner = userRepository.save(owner1);

        // Create a category
        Category it = Category.builder().name("new category2").parent(true).build();

        // Saving the categories
        categoryRepository.save(it);

        // Building a user
        User borrower1 = User.builder().firstName("firstName").lastName("lastName")
                .email("user20.otherName@hotmail.com").password("pass1word").build();

        // Saving the user
        User borrower = userRepository.save(borrower1);

        // Create an ad
        AdDto speaker1 = AdDto.builder().title("Title 1 for test").description("Renting out a brand new speaker")
                .rental(true).durationType(AdType.WEEK).duration(2).price(100).streetAddress("vollabakken 3")
                .postalCode(7030).city("Trondheim").userId(owner.getId()).categoryId(it.getId()).build();
        AdDto speaker2 = AdDto.builder().title("Title 2 for test").description("Renting out a brand new speaker")
                .rental(true).durationType(AdType.WEEK).duration(2).price(100).streetAddress("vollabakken 3")
                .postalCode(7030).city("Trondheim").userId(owner.getId()).categoryId(it.getId()).build();

        // Persist ads
        adService.postNewAd(speaker1);
        adService.postNewAd(speaker2);

        // Find ads
        Set<Ad> foundAds1 = adRepository.findByTitle("Title 1 for test");
        Set<Ad> foundAds2 = adRepository.findByTitle("Title 2 for test");

        Optional<Ad> foundAd1 = foundAds1.stream().findFirst();
        Optional<Ad> foundAd2 = foundAds2.stream().findFirst();

        assertNotNull(foundAd1);
        assertNotNull(foundAd2);

        // Create rentals
        Rental rental1 = Rental.builder().dateOfRental(LocalDate.now()).rentFrom(LocalDate.now().minusDays(2))
                .rentTo(LocalDate.now().plusWeeks(1)).deadline(LocalDate.now().minusDays(1)).borrower(borrower)
                .owner(owner).ad(foundAd1.get()).active(true).price(100).build();
        Rental rental2 = Rental.builder().dateOfRental(LocalDate.now()).rentFrom(LocalDate.now().minusDays(2))
                .rentTo(LocalDate.now().plusWeeks(1)).deadline(LocalDate.now().minusDays(1)).borrower(borrower)
                .owner(owner).ad(foundAd2.get()).active(true).price(100).build();

        // Persist both rentals
        rentalRepository.save(rental1);
        rentalRepository.save(rental2);

        // Retrieve both rentals
        ResponseEntity<Object> res = rentalService.getRentalsByUserId(owner.getId());

        // Get the two ad ids
        long foundAd1Id = foundAd1.get().getId();
        long foundAd2Id = foundAd2.get().getId();

        // Assert that the body of the response contains both ads!
        assertTrue(res.getBody().toString().contains("adId=" + foundAd1Id));
        assertTrue(res.getBody().toString().contains("adId=" + foundAd2Id));

    }
}
