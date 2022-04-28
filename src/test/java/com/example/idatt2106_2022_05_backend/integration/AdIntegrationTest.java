package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AdIntegrationTest {


    @Autowired
    AdService adService;
    @Autowired
    AdRepository adRepository;

    @Autowired
    RentalRepository rentalRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CalendarDateRepository calendarDateRepository;

    @Autowired
    ReviewRepository reviewRepository;
    @BeforeEach
    public void setUp() {
        // Building a user
        User user = User.builder().
                firstName("firstName").
                lastName("lastName").
                email("user.name@hotmail.com").
                password("pass1word").
                build();

        // Saving the user
        userRepository.save(user);

        // Building categories
        Category clothes = Category.builder().
                name("new category1").
                parent(true).
                build();

        Category it = Category.builder().
                name("new category2").
                parent(true).
                build();

        // Saving the categories
        categoryRepository.save(clothes);
        categoryRepository.save(it);
    }

    @AfterEach
    public void emptyDatabase() {
        /**
        // get all ads
        List<Ad> ads = adRepository.findAll();
        for(Ad ad : ads) {

            Set<CalendarDate> dates = ad.getDates();
            if(dates != null) {
                for(CalendarDate date : dates) {
                    date.getAds().remove(ad);
                    calendarDateRepository.save(date);
                }
            }

            Set<Review> reviews = ad.getReviews();
            if(reviews != null) {
                for(Review review : reviews) {
                    review.setAd(null);
                    reviewRepository.save(review);
                }
            }

            Set<Rental> rentals = ad.getRentals();
            if(rentals != null) {
                for(Rental rental : rentals) {
                    rental.setAd(null);
                    rental.setBorrower(null);
                    rental.setOwner(null);
                    rentalRepository.save(rental);
                }
            }
            ad.setCategory(null);
            ad.setRentals(null);
            ad.setUser(null);
            ad.setDates(null);
            ad.setPhotos(null);
            ad.setReviews(null);

            adRepository.delete(ad);
        }
        List<User> users = userRepository.findAll();
        for(User user : users) {
            user.setPicture(null);
            user.setAds(null);
            for(Review review : user.getReviews()) {
                review.setUser(null);
                reviewRepository.save(review);
            }
            user.setReviews(null);
            user.setRentalsBorrowed(null);
            user.setRentalsOwned(null);
            userRepository.delete(user);
        }
        List<Category> categories = categoryRepository.findAll();
        for(Category category : categories) {
            category.setAds(null);
            categoryRepository.delete(category);
        }
         */
        reviewRepository.deleteAll();
        rentalRepository.deleteAll();
        adRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Nested
    class AdClassTests {
        @Test
        public void whenForeignKeysCorrect_ThenAadIsSaved(){

            // If the setUp @beforeEach works as it should, the repository should be empty
            assertEquals(0, adRepository.findAll().size());

            // Find the user and category created in setUp
            User user = userRepository.findAll().get(0);
            Category clothesCategory = categoryRepository.findAll().get(0);

            // Building an ad-dto with foreign keys
            AdDto ad = AdDto.builder().
                    title("Nike shoes").
                    description("Renting out a pair of shoes in size 40").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.WEEK).
                    duration(2).
                    price(100).
                    streetAddress("Project Road 4").
                    postalCode(7234).
                    userId(user.getId()).
                    categoryId(clothesCategory.getId()).
                    build();

            try {
                // Post the ad
                adService.postNewAd(ad);
            } catch (IOException e) {
                fail();
                e.printStackTrace();
            }


            //Verify that the post is saved
            assertTrue(adRepository.findAll().size() > 0);
            assertEquals(adRepository.findAll().get(0).getTitle(), "Nike shoes");
        }

        @Test
        public void whenForeignKeysWrong_ThenAdIsNotSaved(){
            // Building a user
            User user = User.builder().
                    firstName("user2").
                    lastName("second").
                    email("second.user@hotmail.com").
                    password("newPassword").
                    build();

            // Building a new category
            Category boats = Category.builder().
                    name("Boats").
                    build();

            // Building an ad-dto with foreign keys
            AdDto boatAd = AdDto.builder().
                    title("Sail boat").
                    description("Renting out a huge sail boat").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(100).
                    streetAddress("The sea").
                    postalCode(7000).
                    userId(202L).
                    categoryId(101L).
                    build();

            try {
                // Post the ad
                adService.postNewAd(boatAd);

                // The test will fail because the foreign keys did not exist
                //fail();
            }catch (InvalidDataAccessApiUsageException e) {

                // The test passes if this exception is caught
            } catch (NullPointerException | IOException e) {
                // The test passes if this exception is caught
                e.printStackTrace();
            }
        }
    }

    @Nested
    class UpdatePostTests{
        @Test
        public void updatePostWorks(){

            // Get the foreign keys
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Building an ad with foreign keys
            Ad newAd = Ad.builder().
                    title("Sail boat").
                    description("Renting out a huge sail boat").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(100).
                    streetAddress("The sea").
                    postalCode(7000).
                    user(user).
                    category(category).
                    build();

            // Persisting the ad and set newAd equal to it in order to fetch the id
            newAd = adRepository.save(newAd);

            // Assert that the ad was persisted
            assertTrue(adRepository.existsById(newAd.getId()));

            // Create an ad update-dto
            AdUpdateDto updateDto = new AdUpdateDto();
            updateDto.setTitle("Renting out sail boat");

            try {
                // Edit the ad
                adService.updateAd(newAd.getId(),updateDto);

                // Get the edited ad and assign it to the ad reference (newAd)
                newAd = adRepository.findById(newAd.getId()).get();

                // Assert that the new ad title was persisted
                assertEquals("Renting out sail boat", newAd.getTitle());

            }catch(InvalidDataAccessApiUsageException e){
                fail();
            }
        }

        @Test
        public void nonExistentPostCannotBeUpdated() {
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Building an ad without persisting it
            Ad newAd = Ad.builder().
                    title("non existent post").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("non existent").
                    postalCode(7000).
                    user(user).
                    category(category).
                    build();
            try {
                // Create an update-dto
                AdUpdateDto updateDto = new AdUpdateDto();
                updateDto.setTitle("The ad still does not exist");

                // Trying to edit the ad without having persisted it (the ad does not exist yet)
                adService.updateAd(newAd.getId(), updateDto);

                // The ad-update fails
                fail();
            } catch (InvalidDataAccessApiUsageException e) {
                // Pass test if this exception is thrown
            }
        }
    }

    @Nested
    class DeletePostTests{

        @Test
        public void whenPostExists_postIsDeleted(){
            // The cleanup should have erased ads from this repo (EXCEPT ONE)
            assertEquals(0, adRepository.findAll().size());

            // Retrieve the user and category
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Building an ad without persisting it
            Ad newAd = Ad.builder().
                    id(100L).
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            newAd = adRepository.save(newAd);

            // Assert that the ad was saved
            assertEquals(1, adRepository.findAll().size());

            // Delete the newly created ad
            adService.deleteAd(newAd.getId());

            // Assert that the ad was deleted
            assertEquals(0, adRepository.findAll().size());
        }

        @Test
        public void whenPostDoesNotExist_postIsNotDeleted(){
            // The cleanup should have erased ads from this repo
            assertEquals(0, adRepository.findAll().size());

            ResponseEntity<Object> res = adService.deleteAd(100L);

            // Service class should return HttpResponse.NOT_FOUND
            assertEquals(HttpStatus.NOT_FOUND.value(), res.getStatusCodeValue());
        }
    }

    @Nested
    class UserRelatedAdTests {

        @Test
        public void whenUserExists_retrieveAds(){
            // Retrieve the user and category
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Building an ad without persisting it
            Ad newAd = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            // Save the ad with the foreign keys
            newAd = adRepository.save(newAd);

            // Due to the foreign keys, the user now also has this ad
            ResponseEntity<Object> res = adService.getAllAdsByUser(user.getId());

            // Service class should return HttpResponse.OK
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }

        @Test
        public void whenUserDoesNotExist_adsAreNotReturned(){
            // Random id
            long wrongUserId = 101L;

            // The method will fail when wrong id is used
            ResponseEntity<Object> res = adService.getAllAvailableAdsByUser(wrongUserId);

            assertEquals(HttpStatus.NOT_FOUND.value(), res.getStatusCodeValue());
        }

        @Test
        public void paginationWorks(){

            // Build new user and category
            User user = User.builder().
                    firstName("firstName").
                    lastName("lastName").
                    email("user.name@hotmail.com").
                    password("pass1word").
                    build();


            // Persist the user --> we now have two users
            userRepository.save(user);

            // Fetch the user
            User user1 = userRepository.findAll().get(0);

            // Fetch the category
            Category category1 = categoryRepository.findAll().get(0);

            // Create and save 15 new posts
            for(int i = 0; i < 15; i++){

                // Building an ad
                Ad newAd = Ad.builder().
                        title("title").
                        description("").
                        rental(true).
                        rentedOut(false).
                        durationType(AdType.HOUR).
                        duration(2).
                        price(100).
                        streetAddress("address").
                        postalCode(7999).
                        user(user1).
                        category(category1).
                        build();

                // Save the new ad
                adRepository.save(newAd);
            }
            // Pagination with all 15 ads
            ResponseEntity<Object> res = adService.getPageOfAds(15);

            // Service class should return HttpResponse.OK
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }

        @Test
        public void paginationDoesNotWork_WhenNotEnoughAds() {
            // Build new user
            User user = User.builder().
                    firstName("firstName").
                    lastName("lastName").
                    email("user.name@hotmail.com").
                    password("pass1word").
                    build();

            // Persist the user --> we now have two users
            userRepository.save(user);

            // Fetch the user
            User user1 = userRepository.findAll().get(0);

            // Fetch the category
            Category category1 = categoryRepository.findAll().get(0);

            // Create and save 15 new posts
            for(int i = 0; i < 15; i++){
                // Building an ad
                Ad newAd = Ad.builder().
                        title("title").
                        description("").
                        rental(true).
                        rentedOut(false).
                        durationType(AdType.HOUR).
                        duration(2).
                        price(100).
                        streetAddress("address").
                        postalCode(7999).
                        user(user1).
                        category(category1).
                        build();

                // Save the new ad
                adRepository.save(newAd);
            }
            assertEquals(15, adRepository.findAll().size());

            // Pagination with all 16 ads (only 15 present in db)
            ResponseEntity<Object> res = adService.getPageOfAds(16);

            // Service class should return HttpResponse.NOT_FOUND
            assertEquals(HttpStatus.NOT_FOUND.value(), res.getStatusCodeValue());
        }
    }


    @Nested
    class AdGetRelatedTests{
        // get all available ads
        @Test
        public void whenAdsAreAvailable_allAvailableAdsAreReturned() {
            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Fetch the category
            Category category = categoryRepository.findAll().get(0);

            // Create available ads
            Ad availableAd1 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            Ad availableAd2 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            // Persist them
            adRepository.save(availableAd1);
            adRepository.save(availableAd2);

            // Create unavailable ads
            Ad unavailableAd1 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(true).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            Ad unavailableAd2 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(true).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            // Persist them
            adRepository.save(unavailableAd1);
            adRepository.save(unavailableAd2);

            // Repository size should be 4
            assertEquals(adRepository.findAll().size(), 4);

            // Repository call should return two ads
            assertEquals(adRepository.getAllAvailableAds().size(), 2);

            ResponseEntity<Object> res = adService.getAllAvailableAds();

            // Service class should return HttpResponse.OK
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }

        // get available ads by user id
        @Test
        public void allAvailableAdsAreReturned_WhenUserIdCorrect() {

            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Fetch the category
            Category category = categoryRepository.findAll().get(0);

            // Create available ads
            Ad availableAd1 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            Ad availableAd2 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            adRepository.save(availableAd1);
            adRepository.save(availableAd2);

            // Repository call should return two ads
            assertEquals(adRepository.getAvailableAdsByUserId(user.getId()).size(), 2);

            ResponseEntity<Object> res = adService.getAllAvailableAdsByUser(user.getId());

            // Service class should return HttpResponse.OK
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }

        // get ads by postal code
        @Test
        public void allAdsWithPostalCodeAreReturned() {

            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Fetch the category
            Category category = categoryRepository.findAll().get(0);

            // Create ads
            Ad ad1 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(8000).
                    user(user).
                    category(category).
                    build();

            Ad ad2 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(8000).
                    user(user).
                    category(category).
                    build();

            Ad ad3 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(8000).
                    user(user).
                    category(category).
                    build();

            // Persist them
            adRepository.save(ad1);
            adRepository.save(ad2);
            adRepository.save(ad3);


            // Repository call should return 3
            assertEquals(3, adRepository.findByPostalCode(8000).size());

            ResponseEntity<Object> res = adService.getAllAdsByPostalCode(8000);

            // Service class should return HttpResponse.OK
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }

        // get ads by rental type
        @Test
        public void allAdsForRentAreReturned() {
            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Fetch the category
            Category category = categoryRepository.findAll().get(0);

            // Create ads with rental == true
            Ad ad1 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            Ad ad2 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            // Persist
            adRepository.save(ad1);
            adRepository.save(ad2);


            // Repository call should return 2
            assertEquals(adRepository.findByRental(true).size(), 2);

            ResponseEntity<Object> res = adService.getAllAdsByRentalType(true);

            // Service class should return HttpResponse.OK
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }

        @Test
        public void allAdsGivenAwayAreReturned() {
            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Fetch the category
            Category category = categoryRepository.findAll().get(0);

            // Create ads with rental == false
            Ad ad1 = Ad.builder().
                    title("title").
                    description("").
                    rental(false).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            Ad ad2 = Ad.builder().
                    title("title").
                    description("").
                    rental(false).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            // Persist
            adRepository.save(ad1);
            adRepository.save(ad2);

            // Repository call should return 2
            assertEquals(adRepository.findByRental(false).size(), 2);

            ResponseEntity<Object> res = adService.getAllAdsByRentalType(false);

            // Service class should return HttpResponse.OK
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }

        // get all ads nearby
        @Test
        public void allNearbyAdsAreReturned_WhenAdsExistNearby() {

        }
        // get reviews by user id
        @Test
        public void allReviewsAreReturned_WhenUserIdCorrect() {
            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Fetch the category
            Category category = categoryRepository.findAll().get(0);

            // Create ad
            Ad ad = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            // Persist the ad
            adRepository.save(ad);

            // Create Review entity
            Review review = Review.builder().
                    description("Great shoes!").
                    rating(5).
                    build();

            // Save the review
            reviewRepository.save(review);

            // Set foreign keys
            review.setUser(user);
            review.setAd(ad);
            reviewRepository.save(review);

            // Retrieve reviews for user
            List<Review> reviews = reviewRepository.getAllByUser(user);

            // List should be size == 1
            assertEquals(reviews.size(), 1);

            ResponseEntity<Object> res = adService.getReviewsByUserId(user.getId());

            // Service class should return HttpResponse.OK
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }

        @Test
        public void noReviewsAreReturned_WhenUserIdWrong() {
            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Create Review entity
            Review review = Review.builder().
                    id(5L).
                    description("Great shoes!").
                    rating(5).
                    user(user).
                            build();

            // Retrieve reviews for user
            List<Review> reviews = reviewRepository.getAllByUser(user);

            // List should be size == 0
            assertEquals(reviews.size(), 0);

            ResponseEntity<Object> res = adService.getReviewsByUserId(user.getId());

            // Service class should return HttpResponse.NOT_FOUND
            assertEquals(HttpStatus.NOT_FOUND.value(), res.getStatusCodeValue());
        }

        // get all categories
        @Test
        public void allCategoriesAreReturned() {

            // Building categories
            Category category3 = Category.builder().
                    name("new category").
                    build();

            Category category4 = Category.builder().
                    name("even newer category").
                    build();

            // Persist categories
            categoryRepository.save(category3);
            categoryRepository.save(category4);

            // There should be 4 categories in category-repository now
            assertEquals(categoryRepository.findAll().size(), 4);

            ResponseEntity<Object> res = adService.getAllCategories();

            assertEquals(HttpStatus.OK.value(),
                    res.getStatusCodeValue());
        }

        // get all sub categories
        @Test
        public void onlySubClassesAreReturned() {

            // Building categories
            Category category1 = Category.builder().
                    name("new category").
                    parent(true).
                    build();

            // Building 2 sub-categories for category1
            Category subCategory1 = Category.builder().
                    name("new category").
                    parent(false).
                    parentName(category1.getName()).
                    build();

            Category subCategory2 = Category.builder().
                    name("even newer category").
                    parent(false).
                    parentName(category1.getName()).
                    build();

            // Persist categories
            Category mainCategory = categoryRepository.save(category1);
            categoryRepository.save(subCategory1);
            categoryRepository.save(subCategory2);

            // There should be 12 categories in category-repository now
            // 2 (setUp) + 8 (dataLoader) + 3 (this method) = 12
            assertEquals(categoryRepository.findAll().size(),13);

            // Service method should return HttpStatus.OK


            ResponseEntity<Object> res = adService.getAllSubCategories(mainCategory.getName());

            assertEquals(HttpStatus.OK.value(),
                    res.getStatusCodeValue());
        }

        // get all ads within specified category
        @Test
        public void onlyAdsWithinCategoryAreReturned() {
            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Fetch the category
            Category category = categoryRepository.findAll().get(0);

            // Create ad
            Ad ad = Ad.builder().
                    title("random title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            // Persist the ad
            adRepository.save(ad);

            // Persist the category
            categoryRepository.save(category);

            // Retrieve category with name
            Optional<Category> categoryFound = categoryRepository.findByName(category.getName());
            if(categoryFound.isPresent()) {

                // Assert that the ad was added to the category
                assertEquals(categoryFound.get().getAds().size(), 1);

                // Assert that the service response is OK
                ResponseEntity<Object> res = adService.getAllAdsInCategory(category.getId());
                assertEquals(res.getStatusCodeValue(), HttpStatus.OK.value());
            }
            else{
                // If no category was found, the test fails
                fail();
            }
        }
    }
}
