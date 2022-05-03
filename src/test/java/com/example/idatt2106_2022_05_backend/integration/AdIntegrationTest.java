package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.Geocoder;
import com.example.idatt2106_2022_05_backend.util.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    PictureRepository pictureRepository;

    @Autowired
    ReviewRepository reviewRepository;

    private ModelMapper modelMapper = new ModelMapper();


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
        reviewRepository.deleteAll();
        rentalRepository.deleteAll();
        pictureRepository.deleteAll();
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
            } catch (IOException | InterruptedException e) {
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
            } catch (InterruptedException e) {
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
        public void paginationWorks_WhenNotEnoughAds() {
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

            // Pagination with 24 ads (only 15 present in db)
            ResponseEntity<Object> res = adService.getPageOfAds(24);

            // Service class should return HttpResponse.OK
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
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

        @Test
        public void getAllAdsByCity() {
            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Fetch the category
            Category category = categoryRepository.findAll().get(0);

            // Create ad
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
                    city("Bodø").
                    user(user).
                    category(category).
                    build();

            // Create ad
            Ad ad2 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(8001).
                    city("Bodø").
                    user(user).
                    category(category).
                    build();

            // Create ad
            Ad ad3 = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    city("Bodø").
                    postalCode(8002).
                    user(user).
                    category(category).
                    build();

            // Persist ads
            adRepository.save(ad1);
            adRepository.save(ad2);
            adRepository.save(ad3);

            ResponseEntity<Object> res = adService.getAllAdsInCity("Bodø");
            assertTrue(res.getBody().toString().contains("Bodø"));
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
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

            int previousCategoriesCount = categoryRepository.findAll().size();

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
            assertEquals(previousCategoriesCount + 3,categoryRepository.findAll().size());

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
            Set<Category> categoriesFound = categoryRepository.findByName(category.getName());
            if(categoriesFound != null) {
                for(Category category1 : categoriesFound) {
                    // Assert that the ad was added to the category
                    assertEquals(category1.getAds().size(), 1);

                    // Assert that the service response is OK
                    ResponseEntity<Object> res = adService.getAllAdsInCategory(category.getId());
                    assertEquals(res.getStatusCodeValue(), HttpStatus.OK.value());
                }
            }
            else{
                // If no category was found, the test fails
                fail();
            }
        }
    }

    @Nested
    class SortingTests {

        @Test
        public void getAllAdsWithDistance() {

        }

        // Should return ads
        @Test
        public void searchByDistance_WhenAdsExistWithinDistance() {

        }

        // Should return no ads
        @Test
        public void searchByDistance_WhenNoAdsExistWithinDistance() {

        }

        @Test
        public void sortByDescendingTitle() {

        }

        @Test
        public void sortByAscendingTitle() {

        }

        @Test
        public void sortByCreatedDateAscending() {

        }

        @Test
        public void sortByCreatedDateDescending() {

        }

        @Test
        public void adReturned_WhenSearchingForPartsOfExistingTitle() {

        }

        @Test
        public void nothingReturned_WhenSearchingForPartsOfNonExistingTitle() {

        }

        @Test
        public void adsAreSortedByPriceAscending() {

        }

        @Test
        public void adsAreSortedByPriceDescending() {

        }

        @Test
        public void adsAreSortedByDistanceAscending() {

        }

        @Test
        public void adsAreSortedByDistanceDescending() {

        }

        @Test
        public void adsWithinDistanceAreReturned() {

        }

        @Test
        public void adsWithingPriceRanceAreReturned() {

        }

        @Test
        public void adsAreGivenCoordinates_WhenAdExists() {

        }

        /*** BELOW CODE CONTAINS ONLY COPIED METHODS FROM "AdServiceImpl.java" (FOR TESTING PURPOSES) ***/

        public List<AdDto> getAllAdsWithDistance(UserGeoLocation userGeoLocation) throws IOException {
            ArrayList<AdDto> ads = new ArrayList<>();

            for(Ad ad : adRepository.findAll()){
                //Setting all attributes and decompressing pictures in help method
                AdDto adDto = castObject(ad);
                //Calculate and set distance
                adDto.setDistance(calculateDistance(userGeoLocation.getLat(),
                        userGeoLocation.getLng(), ad.getLat(), ad.getLng()));
                //Adding all ads to list and then response
                ads.add(adDto);
            }
            return ads.stream().sorted(Comparator.comparing(AdDto::getDistance))
                    .collect(Collectors.toList());
        }

        private AdDto castObject(Ad ad) {
            AdDto adDto = modelMapper.map(ad, AdDto.class);
            ;

            // decompressing and converting images in support method
            // convertPictures(ad, adDto);
            return adDto;
        }

        public double calculateDistance(double lat1, double long1, double lat2,
                                        double long2) {
            double dist = org.apache.lucene.util.SloppyMath.haversinMeters(lat1, long1, lat2, long2);
            return dist/1000;
        }

        public List<AdDto> sortByDistance(UserGeoLocation userGeoLocation) throws IOException {
            List<AdDto> ads = getAllAdsWithDistance(userGeoLocation);
            return ads.stream().limit(userGeoLocation.getAmount())
                    .collect(Collectors.toList());
        }


        public List<AdDto> sortByDescending(int pageSize, String sortBy){
            Pageable pageable = PageRequest.of(0, pageSize, Sort.by(sortBy).descending());
            List<Ad> list =  adRepository.findAll(pageable).get().collect(Collectors.toList());
            return list.stream()
                    .map(ad -> modelMapper.map(ad, AdDto.class))
                    .collect(Collectors.toList());

        }


        public List<AdDto> sortByAscending(int pageSize, String sortBy){
            Pageable pageable = PageRequest.of(0, pageSize, Sort.by(sortBy).ascending());
            List<Ad> list = adRepository.findAll(pageable).get().collect(Collectors.toList());
            return list.stream().map(ad -> modelMapper.map(ad, AdDto.class)).
                    collect(Collectors.toList());
        }


        public Stream<AdDto> sortByCreatedDateAscending(int pageSize){
            List<Ad> ads = adRepository.findAll();
            ads.sort(Comparator.comparing(Ad::getCreated));
            return ads.stream()
                    .map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList()).stream()
                    .limit(pageSize);
        }


        public Stream<AdDto> sortByCreatedDateDescending(int pageSize){
            List<Ad> ads = adRepository.findAll();
            ads.sort(Comparator.comparing(Ad::getCreated).reversed());
            return ads.stream()
                    .map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList()).stream()
                    .limit(pageSize);
        }

        public List<AdDto> searchThroughAds(String searchWord){
            //List to be filled with corresponding ads
            List<Ad> adsContainingSearchWord = new ArrayList<>();

            List<Ad> ads = adRepository.findAll();

            //Checking all titles for searchWord
            for(Ad a: ads){
                if(a.getTitle().toLowerCase().contains(searchWord.toLowerCase())){
                    adsContainingSearchWord.add(a);
                }
            }
            List<Category> categories = categoryRepository.findAll();

            //Adding all ads with the category
            for(Category c: categories){
                if(c.getName().toLowerCase().contains(searchWord.toLowerCase())) {
                    for (Ad a : c.getAds()) {
                        if(!adsContainingSearchWord.contains(a)) {
                            adsContainingSearchWord.add(a);
                        }
                    }
                }
            }

            //Casting objects to Dto and returning
            return adsContainingSearchWord.stream()
                    .map(ad1 -> modelMapper.map(ad1, AdDto.class)).collect(Collectors.toList());
        }

        public List<AdDto> sortArrayByPriceAscending(List<AdDto> list){
            list.sort(Comparator.comparing(AdDto::getPrice));
            return list;        }

        public List<AdDto> sortArrayByPriceDescending(List<AdDto> list){
            list.sort(Comparator.comparing(AdDto::getPrice).reversed());
            return list;        }

        public List<AdDto> sortArrayByDistanceAscending(List<AdDto> list){
            list.sort(Comparator.comparing(AdDto::getDistance));
            return list;        }

        public List<AdDto> sortArrayByDistanceDescending(List<AdDto> list){
            list.sort(Comparator.comparing(AdDto::getDistance).reversed());
            return list;        }

        public List<AdDto> getListWithinDistanceInterval(List<AdDto> list, double limit){
            list.stream().filter(x -> x.getDistance()<limit).collect(Collectors.toList());
            return list;
        }

        public List<AdDto> getListOfAdsWithinPriceRange(List<AdDto> list, double upperLimit, double lowerLimit){
            list.stream().filter(x->lowerLimit<x.getPrice() && x.getPrice()<upperLimit).collect(Collectors.toList());
            return list;
        }

        private void setCoordinatesOnAd(Ad ad)
                throws IOException, InterruptedException {
            ObjectMapper objectMapper = new ObjectMapper();
            Geocoder geocoder = new Geocoder();

            String response = geocoder.GeocodeSync(ad.getStreetAddress() + ad.getPostalCode() + ad.getCity());
            JsonNode responseJSONNode = objectMapper.readTree(response);
            JsonNode items = responseJSONNode.get("items");

            for(JsonNode item : items){
                JsonNode address = item.get("address");
                String label = address.get("label").asText();
                JsonNode position = item.get("position");

                String lat = position.get("lat").asText();
                String lng = position.get("lng").asText();
                System.out.println(label + " is located at " + lat + "," + lng + ".");
                if(!lng.equals("") && !lat.equals("")) {
                    ad.setLat(Double.parseDouble(lat));
                    ad.setLng(Double.parseDouble(lng));
                }
            }
        }

    }
}
