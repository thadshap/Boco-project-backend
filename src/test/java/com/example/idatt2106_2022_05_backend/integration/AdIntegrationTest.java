package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.Review;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CategoryRepository;
import com.example.idatt2106_2022_05_backend.repository.ReviewRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

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
                name("Shoes").
                parent(true).
                build();

        Category it = Category.builder().
                name("IT").
                parent(true).
                build();

        // Saving the categories
        categoryRepository.save(clothes);
        categoryRepository.save(it);
    }

    @AfterEach
    public void emptyDatabase() {
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
                    userId(user.getId()).
                    categoryId(boats.getId()).
                    build();

            try {
                // Post the ad
                adService.postNewAd(boatAd);
                // The test will fail because the foreign keys did not exist
                fail();
            }catch(NoSuchElementException e){
                // todo the test will not receive this exception because the method does not throw it
                // todo cast this exception in adService
                // The test will catch this exception when the foreign keys do not exist
                /**
            }catch(IllegalAccessException e){
                // The test fails if this exception is caught
                fail();
                 */
            } catch (IOException e) {

                // The test fails if this exception is caught
                fail();
                e.printStackTrace();
            }
        }

        @Test
        public void user_in_org_can_add_post_to_org(){
            //Add user to organization
            User user = userRepository.findAll().get(0);
            Organization organization = orgRepository.findAll().get(0);
            UserOrganization userOrg = new UserOrganization(user, organization, OrganizationRole.EMPLOYEE);
            userOrgRepository.save(userOrg);

            //Add post in organization from user
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", user, organization, new HashSet<>());
            try{
                postService.add(post, user);
            }catch(IllegalAccessException | NoSuchElementException e){
                fail();
            }
        }

        @Test
        public void user_out_of_org_cant_add_post_to_org(){
            User user = userRepository.findAll().get(0);
            Organization organization = orgRepository.findAll().get(0);

            //Try to add post in organization from user
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", user, organization, new HashSet<>());
            try{
                postService.add(post, user);
                fail();
            }catch(IllegalAccessException e){
                //pass test
            }catch(NoSuchElementException e){
                fail();
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

            }catch(NoSuchElementException e){
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
            } catch (NoSuchElementException e) {
                // Pass test if this exception is thrown
            }
        }
    }

    @Nested
    class DeletePostTests{

        @Test
        public void whenPostExists_postIsDeleted(){
            // The cleanup should have erased ads from this repo
            assertEquals(0, adRepository.findAll().size());

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

            newAd= adRepository.save(newAd);

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

            // Fetch the user
            User user = userRepository.findAll().get(0);

            try {
                // Try to delete an ad (the repo is empty --> this should fail)
                adService.deleteAd(1L);
                fail();
            }
            catch(NoSuchElementException e){
                // Test passed
            }
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
            Response response = adService.getAllAdsByUser(user.getId());

            // Assert that the response and the ad is equal
            assertEquals(newAd, response.getObject());
            assertEquals(newAd, response.getBody());
            assertEquals(HttpStatus.OK, response.getStatus());
        }

        @Test
        public void whenUserDoesNotExist_adsAreNotReturned(){
            User user = userRepository.findAll().get(0);

            // Random id
            Long wrongUserId = 101L;

            try {
                // The method will fail when wrong id is used
                adService.getAllAvailableAdsByUser(wrongUserId);
                fail();

            }catch(NoSuchElementException e){
                // Method works correctly if this exception is caught
            }
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
            Response response = adService.getPageOfAds(15);
            assertEquals(response.getStatus(), HttpStatus.OK);
        }

        @Test
        public void paginationDoesNotWork_WhenNotEnoughAds(){

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
            // Pagination with all 16 ads (only 15 present in db)
            Response response = adService.getPageOfAds(16);
            assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST); //TODO this is not the actual
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

            // Service call should return HttpResponse.OK
            Response response = adService.getAllAvailableAds();
            System.out.println(response.getBody()); //todo remove after
            assertEquals(response.getStatus(), HttpStatus.OK);
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

            // Repository call should return two ads
            assertEquals(adRepository.getAvailableAdsByUserId(user.getId()).size(), 2);

            // Service call should return HttpResponse.OK
            Response response = adService.getAllAvailableAdsByUser(user.getId());
            assertEquals(response.getStatus(), HttpStatus.OK);
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

            Ad ad3 = Ad.builder().
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
            adRepository.save(ad1);
            adRepository.save(ad2);
            adRepository.save(ad3);


            // Repository call should return 3
            assertEquals(adRepository.findByPostalCode(7999).size(), 3);

            // Service call should return HttpResponse.OK
            assertEquals(adService.getAllAdsByPostalCode(7999).getStatus(), HttpStatus.OK);
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

            // Service call should return HttpResponse.OK
            assertEquals(adService.getAllAdsByRentalType(true).getStatus(), HttpStatus.OK);
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

            // Service call should return HttpResponse.OK
            assertEquals(adService.getAllAdsByRentalType(false).getStatus(), HttpStatus.OK);
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
                    ad(ad).
                    user(user).
                    build();

            // Retrieve reviews for user
            List<Review> reviews = reviewRepository.getAllByUser(user);

            // List should be size == 2
            assertEquals(reviews.size(), 2);

            // Service class should return HttpResponse.OK
            assertEquals(adService.getReviewsByUserId(user.getId()).getStatus(),
                    HttpStatus.OK);
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

            // Retrieve reviews for user  // TODO does this return null?
            List<Review> reviews = reviewRepository.getAllByUser(user);

            // List should be size == 0
            assertEquals(reviews.size(), 0);

            // Service class should return HttpResponse.NOT_FOUND //TODO correct response or other response?
            assertEquals(adService.getReviewsByUserId(user.getId()).getStatus(),
                    HttpStatus.NOT_FOUND);
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

            // Service method should return HttpStatus.OK
            assertEquals(adService.getAllCategories().getStatus(), HttpStatus.OK); //todo create method
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

            // There should be 5 categories in category-repository now
            assertEquals(categoryRepository.findAll().size(), 5);

            // Service method should return HttpStatus.OK
            assertEquals(adService.getAllSubCategories(mainCategory.getName()).getStatus(), HttpStatus.OK); //todo create method
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
            Ad savedAd = adRepository.save(ad);

            // Retrieve all ads within category
            Optional<Category> categoryFound = categoryRepository.findByName(category.getName());
            if(categoryFound.isPresent()) {

                // Assert that the ad was added to the category
                assertEquals(categoryFound.get().getAds().size(), 1);
            }
            else{
                // If no category was found, the test fails
                fail();
            }
            // Assert that the service response is OK
            Response response = adService.getAllAdsInCategory(savedAd.getId());
            assertEquals(response.getStatus(), HttpStatus.OK);
        }
    }
}
