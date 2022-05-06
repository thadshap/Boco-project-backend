package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.dto.ad.FilterListOfAds;
import com.example.idatt2106_2022_05_backend.dto.user.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.Geocoder;
import lombok.SneakyThrows;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    MessageRepository messageRepository;

    @Autowired
    ReviewRepository reviewRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    public void setUp() {
        // Building a user
        User user = User.builder().firstName("firstName").lastName("lastName").email("user.name@hotmail.com")
                .password("pass1word").build();

        // Saving the user
        userRepository.save(user);

        // Building categories
        Category clothes = Category.builder().name("new category1").parent(true).build();

        Category it = Category.builder().name("new category2").parent(true).build();

        // Saving the categories
        categoryRepository.save(clothes);
        categoryRepository.save(it);
    }

    @AfterEach
    public void emptyDatabase() {
        reviewRepository.deleteAll();
        rentalRepository.deleteAll();
        pictureRepository.deleteAll();
        messageRepository.deleteAll();
        userRepository.deleteAll();
        adRepository.deleteAll();
        // messageRepository.deleteAll();
        // outputMessageRepository.deleteAll();
        // userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Nested
    class AdClassTests {
        @Test
        public void whenForeignKeysCorrect_ThenAadIsSaved() {

            // If the setUp @beforeEach works as it should, the repository should be empty
            assertEquals(0, adRepository.findAll().size());

            // Find the user and category created in setUp
            User user = userRepository.findAll().get(0);
            Category clothesCategory = categoryRepository.findAll().get(0);

            // Building an ad-dto with foreign keys
            AdDto ad = AdDto.builder().title("Nike shoes").description("Renting out a pair of shoes in size 40")
                    .rental(true).rentedOut(false).durationType(AdType.WEEK).price(100).streetAddress("Project Road 4")
                    .postalCode(7234).userId(user.getId()).categoryId(clothesCategory.getId()).build();

            try {
                // Post the ad
                adService.postNewAd(ad);
            } catch (IOException | InterruptedException e) {
                fail();
                e.printStackTrace();
            }

            // Verify that the post is saved
            assertTrue(adRepository.findAll().size() > 0);
            assertEquals(adRepository.findAll().get(0).getTitle(), "Nike shoes");
        }

        @Test
        public void whenForeignKeysWrong_ThenAdIsNotSaved() {
            // Building a user
            User user = User.builder().firstName("user2").lastName("second").email("second.user@hotmail.com")
                    .password("newPassword").build();

            // Building a new category
            Category boats = Category.builder().name("Boats").build();

            // Building an ad-dto with foreign keys
            AdDto boatAd = AdDto.builder().title("Sail boat").description("Renting out a huge sail boat").rental(true)
                    .rentedOut(false).durationType(AdType.MONTH).price(100).streetAddress("The sea").postalCode(7000)
                    .userId(202L).categoryId(101L).build();

            try {
                // Post the ad
                adService.postNewAd(boatAd);

                // The test will fail because the foreign keys did not exist
                // fail();
            } catch (InvalidDataAccessApiUsageException e) {

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
    class UpdatePostTests {
        @Test
        public void updatePostWorks() {

            // Get the foreign keys
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Building an ad with foreign keys
            Ad newAd = Ad.builder().title("Sail boat").description("Renting out a huge sail boat").rental(true)
                    .rentedOut(false).durationType(AdType.MONTH).price(100).streetAddress("The sea").postalCode(7000)
                    .user(user).category(category).build();

            // Persisting the ad and set newAd equal to it in order to fetch the id
            newAd = adRepository.save(newAd);

            // Assert that the ad was persisted
            assertTrue(adRepository.existsById(newAd.getId()));

            // Create an ad update-dto
            AdUpdateDto updateDto = new AdUpdateDto();
            updateDto.setTitle("Renting out sail boat");

            try {
                // Edit the ad
                adService.updateAd(newAd.getId(), updateDto);

                // Get the edited ad and assign it to the ad reference (newAd)
                newAd = adRepository.findById(newAd.getId()).get();

                // Assert that the new ad title was persisted
                assertEquals("Renting out sail boat", newAd.getTitle());

            } catch (InvalidDataAccessApiUsageException e) {
                fail();
            }
        }

        @Test
        public void nonExistentPostCannotBeUpdated() {
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Building an ad without persisting it
            Ad newAd = Ad.builder().title("non existent post").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("non existent").postalCode(7000).user(user)
                    .category(category).build();
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
    class DeleteAdTests {

        @Test
        public void whenAdExists_postIsDeleted() throws IOException, InterruptedException {
            // The cleanup should have erased ads from this repo (EXCEPT ONE)
            assertEquals(0, adRepository.findAll().size());

            // Retrieve the user and category
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Building an ad without persisting it
            AdDto newAd = AdDto.builder().title("ad with a very specific title").description("").rental(true)
                    .rentedOut(false).durationType(AdType.HOUR).price(100).streetAddress("address").postalCode(7999)
                    .userId(user.getId()).categoryId(category.getId()).build();

            adService.postNewAd(newAd);

            // Retrieve the ad
            Set<Ad> ads = adRepository.findByTitle("ad with a very specific title");
            assertEquals(1, ads.size());

            Ad ad = ads.stream().findFirst().get();

            // Assert that the ad was saved
            assertEquals(1, adRepository.findAll().size());

            // Delete the newly created ad
            adService.deleteAd(ad.getId());

            // Assert that the ad was deleted
            assertEquals(0, adRepository.findAll().size());
        }

        @Test
        public void whenAdDoesNotExist_postIsNotDeleted() {
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
        public void whenUserExists_retrieveAds() {
            // Retrieve the user and category
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Building an ad without persisting it
            Ad newAd = Ad.builder().title("title").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("address").postalCode(7999).user(user)
                    .category(category).build();

            // Save the ad with the foreign keys
            newAd = adRepository.save(newAd);

            // Due to the foreign keys, the user now also has this ad
            ResponseEntity<Object> res = adService.getAllAdsByUser(user.getId());

            // Service class should return HttpResponse.OK
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }

        @Test
        public void whenUserDoesNotExist_adsAreNotReturned() {
            // Random id
            long wrongUserId = 101L;

            // The method will fail when wrong id is used
            ResponseEntity<Object> res = adService.getAllAvailableAdsByUser(wrongUserId);

            assertEquals(HttpStatus.NO_CONTENT.value(), res.getStatusCodeValue());
        }

        @Test
        public void paginationWorks() {

            // Build new user and category
            User user = User.builder().firstName("firstName").lastName("lastName").email("user.name@hotmail.com")
                    .password("pass1word").build();

            // Persist the user --> we now have two users
            userRepository.save(user);

            // Fetch the user
            User user1 = userRepository.findAll().get(0);

            // Fetch the category
            Category category1 = categoryRepository.findAll().get(0);

            // Create and save 15 new posts
            for (int i = 0; i < 15; i++) {

                // Building an ad
                Ad newAd = Ad.builder().title("title").description("").rental(true).rentedOut(false)
                        .durationType(AdType.HOUR).price(100).streetAddress("address").postalCode(7999).user(user1)
                        .category(category1).build();

                // Save the new ad
                adRepository.save(newAd);
            }
            // Pagination with all 15 ads
            UserGeoLocation dto = new UserGeoLocation();
            dto.setLat(63.52);
            dto.setLng(15.23);
            // dto.setAmount(1);
            ResponseEntity<Object> res = adService.getPageOfAds(15, dto);

            // Service class should return HttpResponse.OK
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }

        @Test
        public void paginationWorks_WhenNotEnoughAds() {
            // Build new user
            User user = User.builder().firstName("firstName").lastName("lastName").email("user.name@hotmail.com")
                    .password("pass1word").build();

            // Persist the user --> we now have two users
            userRepository.save(user);

            // Fetch the user
            User user1 = userRepository.findAll().get(0);

            // Fetch the category
            Category category1 = categoryRepository.findAll().get(0);

            // Create and save 15 new posts
            for (int i = 0; i < 15; i++) {
                // Building an ad
                Ad newAd = Ad.builder().title("title").description("").rental(true).rentedOut(false)
                        .durationType(AdType.HOUR).price(100).streetAddress("address").postalCode(7999).user(user1)
                        .category(category1).build();

                // Save the new ad
                adRepository.save(newAd);
            }
            assertEquals(15, adRepository.findAll().size());

            // Pagination with 24 ads (only 15 present in db)
            UserGeoLocation dto = new UserGeoLocation();
            dto.setLat(63.52);
            dto.setLng(15.23);
            // dto.setAmount(10);
            ResponseEntity<Object> res = adService.getPageOfAds(24, dto);

            // Service class should return HttpResponse.OK
            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }
    }

    @Nested
    class AdGetRelatedTests {
        // get all available ads
        @Test
        public void whenAdsAreAvailable_allAvailableAdsAreReturned() {
            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Fetch the category
            Category category = categoryRepository.findAll().get(0);

            // Create available ads
            Ad availableAd1 = Ad.builder().title("title").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("address").postalCode(7999).user(user)
                    .category(category).build();

            Ad availableAd2 = Ad.builder().title("title").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("address").postalCode(7999).user(user)
                    .category(category).build();

            // Persist them
            adRepository.save(availableAd1);
            adRepository.save(availableAd2);

            // Create unavailable ads
            Ad unavailableAd1 = Ad.builder().title("title").description("").rental(true).rentedOut(true)
                    .durationType(AdType.HOUR).price(100).streetAddress("address").postalCode(7999).user(user)
                    .category(category).build();

            Ad unavailableAd2 = Ad.builder().title("title").description("").rental(true).rentedOut(true)
                    .durationType(AdType.HOUR).price(100).streetAddress("address").postalCode(7999).user(user)
                    .category(category).build();

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
            Ad availableAd1 = Ad.builder().title("title").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("address").postalCode(7999).user(user)
                    .category(category).build();

            Ad availableAd2 = Ad.builder().title("title").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).lat(63.401920).lng(10.443579).city("Trondheim")
                    .streetAddress("address").postalCode(7999).user(user).category(category).build();

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
            Ad ad1 = Ad.builder().title("title").description("").rental(true).rentedOut(false).durationType(AdType.HOUR)
                    .price(100).streetAddress("address").postalCode(8000).user(user).category(category).build();

            Ad ad2 = Ad.builder().title("title").description("").rental(true).rentedOut(false).durationType(AdType.HOUR)
                    .price(100).streetAddress("address").postalCode(8000).user(user).category(category).build();

            Ad ad3 = Ad.builder().title("title").description("").rental(true).rentedOut(false).durationType(AdType.HOUR)
                    .price(100).streetAddress("address").postalCode(8000).user(user).category(category).build();

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
            Ad ad1 = Ad.builder().title("title").description("").rental(true).rentedOut(false).durationType(AdType.HOUR)
                    .price(100).streetAddress("address").postalCode(7999).user(user).category(category).build();

            Ad ad2 = Ad.builder().title("title").description("").rental(true).rentedOut(false).durationType(AdType.HOUR)
                    .price(100).streetAddress("address").postalCode(7999).user(user).category(category).build();

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
            Ad ad1 = Ad.builder().title("title").description("").rental(false).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("address").postalCode(7999).user(user)
                    .category(category).build();

            Ad ad2 = Ad.builder().title("title").description("").rental(false).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("address").postalCode(7999).user(user)
                    .category(category).build();

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
            Ad ad1 = Ad.builder().title("title").description("").rental(true).rentedOut(false).durationType(AdType.HOUR)
                    .price(100).streetAddress("address").postalCode(8000).city("Bodø").user(user).category(category)
                    .build();

            // Create ad
            Ad ad2 = Ad.builder().title("title").description("").rental(true).rentedOut(false).durationType(AdType.HOUR)
                    .price(100).streetAddress("address").postalCode(8001).city("Bodø").user(user).category(category)
                    .build();

            // Create ad
            Ad ad3 = Ad.builder().title("title").description("").rental(true).rentedOut(false).durationType(AdType.HOUR)
                    .price(100).streetAddress("address").city("Bodø").postalCode(8002).user(user).category(category)
                    .build();

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
            Ad ad = Ad.builder().title("title").description("").rental(true).rentedOut(false).durationType(AdType.HOUR)
                    .price(100).streetAddress("address").postalCode(7999).user(user).category(category).build();

            // Persist the ad
            adRepository.save(ad);

            // Create Review entity
            Review review = Review.builder().description("Great shoes!").rating(5).build();

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
            Review review = Review.builder().id(5L).description("Great shoes!").rating(5).user(user).build();

            // Retrieve reviews for user
            List<Review> reviews = reviewRepository.getAllByUser(user);

            // List should be size == 0
            assertEquals(reviews.size(), 0);

            ResponseEntity<Object> res = adService.getReviewsByUserId(user.getId());

            // Service class should return HttpResponse.NOT_FOUND
            assertEquals(HttpStatus.NO_CONTENT.value(), res.getStatusCodeValue());
        }

        // get all categories
        @Test
        public void allCategoriesAreReturned() {

            // Building categories
            Category category3 = Category.builder().name("new category").build();

            Category category4 = Category.builder().name("even newer category").build();

            // Persist categories
            categoryRepository.save(category3);
            categoryRepository.save(category4);

            // There should be 4 categories in category-repository now
            assertEquals(categoryRepository.findAll().size(), 4);

            ResponseEntity<Object> res = adService.getAllCategories();

            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }

        // get all sub categories
        @Test
        public void onlySubClassesAreReturned() {

            int previousCategoriesCount = categoryRepository.findAll().size();

            // Building categories
            Category category1 = Category.builder().name("new category").parent(true).build();

            // Building 2 sub-categories for category1
            Category subCategory1 = Category.builder().name("new category").parent(false)
                    .parentName(category1.getName()).build();

            Category subCategory2 = Category.builder().name("even newer category").parent(false)
                    .parentName(category1.getName()).build();

            // Persist categories
            Category mainCategory = categoryRepository.save(category1);
            categoryRepository.save(subCategory1);
            categoryRepository.save(subCategory2);

            // There should be 12 categories in category-repository now
            // 2 (setUp) + 8 (dataLoader) + 3 (this method) = 12
            assertEquals(previousCategoriesCount + 3, categoryRepository.findAll().size());

            // Service method should return HttpStatus.OK

            ResponseEntity<Object> res = adService.getAllSubCategories(mainCategory.getName());

            assertEquals(HttpStatus.OK.value(), res.getStatusCodeValue());
        }

        // get all ads within specified category
        @Test
        public void onlyAdsWithinCategoryAreReturned() {
            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Fetch the category
            Category category = categoryRepository.findAll().get(0);

            // Create ad
            Ad ad = Ad.builder().title("random title").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("address").postalCode(7999).user(user)
                    .category(category).build();

            // Persist the ad
            adRepository.save(ad);

            // Persist the category
            categoryRepository.save(category);

            // Retrieve category with name
            Set<Category> categoriesFound = categoryRepository.findByName(category.getName());
            if (categoriesFound != null) {
                for (Category category1 : categoriesFound) {
                    // Assert that the ad was added to the category
                    assertEquals(category1.getAds().size(), 1);

                    // Assert that the service response is OK
                    ResponseEntity<Object> res = adService.getAllAdsInCategory(category.getId());
                    assertEquals(res.getStatusCodeValue(), HttpStatus.OK.value());
                }
            } else {
                // If no category was found, the test fails
                fail();
            }
        }
    }

    @Nested
    class GeoCoderTests {

        public Geocoder geocoder = new Geocoder();

        @SneakyThrows
        @Test
        public void coordinatesAreCalculatedForAdWithCoordinates() {
            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Fetch the category
            Category category = categoryRepository.findAll().get(0);

            // Creating ad without coordinates
            Ad ad = Ad.builder().title("random title").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("fjordvegen 2").postalCode(9990)
                    .city("båtsfjord").user(user).category(category).build();

            // Persist the ad
            adRepository.save(ad);

            String coordinates = geocoder.GeocodeSync("fjordvegen 2 9990 båtsfjord");

            assertNotNull(coordinates);

            // The latitude of the address is 70.62993 (Google Maps)
            assertTrue(coordinates.contains("70.62"));
        }

        @SneakyThrows
        @Test
        public void postingAdAlsoGeneratesCoordinates() {
            // Fetch the user
            User user = userRepository.findAll().get(0);

            // Fetch the category
            Category category = categoryRepository.findAll().get(0);

            // Creating ad without coordinates
            AdDto ad = AdDto.builder().title("random title").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("fjordvegen 2").postalCode(9990)
                    .city("båtsfjord").userId(user.getId()).categoryId(category.getId()).build();

            // Persist the ad
            ResponseEntity<Object> response = adService.postNewAd(ad);

            // Get the id
            long id = (long) response.getBody();

            // Retrieve the new ad
            Optional<Ad> adFound = adRepository.findById(id);

            assertNotNull(adFound);
            if (adFound.isPresent()) {
                // See if the new ad has the correct coordinates
                double latitude = adFound.get().getLat();
                assertTrue(Double.toString(latitude).contains("70.62"));
            } else {
                fail();
            }
        }
    }

    @Nested
    class SortingTests {

        @SneakyThrows
        @Test
        public void getAllAdsSortedByDistance() {
            User user1 = userRepository.findAll().get(0);
            Category category1 = categoryRepository.findAll().get(0);

            // Creating ad without coordinates
            AdDto ad1 = AdDto.builder().title("random title").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("fjordvegen 2").postalCode(9990)
                    .city("båtsfjord").userId(user1.getId()).categoryId(category1.getId()).build();
            AdDto ad2 = AdDto.builder().title("random title").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("fjordvegen 2").postalCode(9990)
                    .city("båtsfjord").userId(user1.getId()).categoryId(category1.getId()).build();
            AdDto ad3 = AdDto.builder().title("random title").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("fjordvegen 2").postalCode(9990)
                    .city("båtsfjord").userId(user1.getId()).categoryId(category1.getId()).build();

            // Persist the ad
            adService.postNewAd(ad1);
            adService.postNewAd(ad2);
            adService.postNewAd(ad3);

            // Verify that the db contains at least 3 ads
            assertTrue(adRepository.findAll().size() >= 3);

            // Simulate a UserGeoLocation (current position of the user activating the method)
            UserGeoLocation userGeoLocation = new UserGeoLocation();
            userGeoLocation.setLat(63.418735);
            userGeoLocation.setLng(10.404052);

            // Amount == the number of ads requested
            // userGeoLocation.setAmount(3);

            // Call on the method using the service-class in order to verify the HTTP-response
            ResponseEntity<Object> res = adService.getAllAdsWithDistance(userGeoLocation);

            // Call on the copied method in order to easily verify the correctness of the ResponseBody
            List<AdDto> DTOs = getAllAdsWithDistance(userGeoLocation);

            // Verify that the list contains 3 ads
            assertEquals(adRepository.findAll().size(), DTOs.size());
        }

        // Should return ads
        @SneakyThrows
        @Test
        public void getAdsWithCategoryAndFilter() {
            User user = userRepository.findAll().get(0);

            assertNotNull(user);

            // Create two categories
            Category category1 = Category.builder().name("The parent of the two categories").parent(true).child(false)
                    .build();

            Category category2 = Category.builder().name("The child of the two categories")
                    .parentName(category1.getName()).parent(false).child(true).build();

            // Persisting the categories
            categoryRepository.save(category1);
            categoryRepository.save(category2);

            assertNotNull(category1);
            assertNotNull(category2);

            // Create a couple of ads with different prices and addresses (but with the same category)
            AdDto adDto1 = AdDto.builder().title("random title1").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(10).streetAddress("fjordvegen 2").postalCode(9990)
                    .city("båtsfjord").userId(user.getId()).categoryId(category1.getId()).build();

            AdDto adDto2 = AdDto.builder().title("random title2").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(100).streetAddress("Rønvikgata 5").postalCode(8006).city("bodø")
                    .userId(user.getId()).categoryId(category1.getId()).build();

            AdDto adDto3 = AdDto.builder().title("random title3").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(1000).streetAddress("Kjerkgata 3").postalCode(7374).city("røros")
                    .userId(user.getId()).categoryId(category1.getId()).build();

            // Creating an ad with a different category
            AdDto adDto4 = AdDto.builder().title("random title4").description("").rental(true).rentedOut(false)
                    .durationType(AdType.HOUR).price(1000).streetAddress("Kjerkgata 3").postalCode(7374).city("røros")
                    .userId(user.getId()).categoryId(category2.getId()).build();

            // Persist the ad
            adService.postNewAd(adDto1);
            adService.postNewAd(adDto2);
            adService.postNewAd(adDto3);
            adService.postNewAd(adDto4);

            // Get the ads
            Optional<Ad> adFound1 = adRepository.findByTitle("random title1").stream().findFirst();
            Optional<Ad> adFound2 = adRepository.findByTitle("random title2").stream().findFirst();
            Optional<Ad> adFound3 = adRepository.findByTitle("random title3").stream().findFirst();
            Optional<Ad> adFound4 = adRepository.findByTitle("random title4").stream().findFirst();

            // Assert that all are found
            assertTrue(adFound1.isPresent());
            assertTrue(adFound2.isPresent());
            assertTrue(adFound3.isPresent());
            assertTrue(adFound4.isPresent());

            Ad ad1 = adFound1.get();
            Ad ad2 = adFound2.get();
            Ad ad3 = adFound3.get();
            Ad ad4 = adFound3.get();

            // Assert that the prices are correct
            assertEquals(10, ad1.getPrice());
            assertEquals(100, ad2.getPrice());
            assertEquals(1000, ad3.getPrice());
            assertEquals(1000, ad4.getPrice());

            // Add the ids to the DTOs
            adDto1.setAdId(ad1.getId());
            adDto2.setAdId(ad2.getId());
            adDto3.setAdId(ad3.getId());
            adDto4.setAdId(ad4.getId());

            // Create a random latitude and longitude simulating the position of the user
            double lat = 63.411080;
            double lng = 10.401449;

            // To perform this method a FilterListOfAds-object is needed
            FilterListOfAds filterListOfAds = new FilterListOfAds();

            // Generating the attributes needed for the filterListOfAds
            List<AdDto> adList = new ArrayList<>();
            adList.add(adDto1);
            adList.add(adDto2);
            adList.add(adDto3);
            adList.add(adDto4);

            assertEquals(4, adList.size());

            // Price and distance (distance) are possible filters
            String filterType1 = "price";
            String filterType2 = "distance";

            // The filters need upper limits
            Double upperPrice = 700.0;
            Double upperDistance = 1000.0;

            // The filters need lower limits
            Double lowerPrice = 9.0;
            Double lowerDistance = 10.0;

            // Only 3 of 4 ads are in this category
            String category = category1.getName();

            // We now have all we need to start creating the object
            filterListOfAds.setList(adList);
            filterListOfAds.setLat(lat);
            filterListOfAds.setLng(lng);

            // Filter ads on price
            filterListOfAds.setFilterType(filterType1);
            filterListOfAds.setLowerLimit(lowerPrice);
            filterListOfAds.setUpperLimit(upperPrice);
            filterListOfAds.setLowestValueFirst(false);

            ResponseEntity<Object> response0 = adService.getAdsWithCategoryAndFilter(filterListOfAds);
            List<AdDto> result0 = getAdsWithCategoryAndFilter(filterListOfAds);
            assertEquals(HttpStatus.OK.value(), response0.getStatusCodeValue());
            assertEquals(result0.size(), 2);

            // Filter ads on price with the lowest value first
            filterListOfAds.setLowestValueFirst(true);
            ResponseEntity<Object> response1 = adService.getAdsWithCategoryAndFilter(filterListOfAds);
            List<AdDto> result1 = getAdsWithCategoryAndFilter(filterListOfAds);
            assertEquals(HttpStatus.OK.value(), response1.getStatusCodeValue());
            assertEquals(result1.size(), 2);
            assertTrue((result1.get(0).getPrice()) > (result1.get(1).getPrice()));

            // Filter ads on price and category --> this should return no more than 3
            // TODO this will also retrieve (due to recursion) the categories that are children of the selected category
            filterListOfAds.setLowestValueFirst(false);
            filterListOfAds.setCategory(category);
            ResponseEntity<Object> response2 = adService.getAdsWithCategoryAndFilter(filterListOfAds);
            List<AdDto> result2 = getAdsWithCategoryAndFilter(filterListOfAds);
            assertEquals(result2.size(), 2);
            assertEquals(HttpStatus.OK.value(), response2.getStatusCodeValue());

            // Filter ads on distance
            filterListOfAds.setFilterType(filterType2);
            filterListOfAds.setLowerLimit(lowerDistance);
            filterListOfAds.setUpperLimit(upperDistance);
            filterListOfAds.setCategory(null); // todo use "" if null gives error

            ResponseEntity<Object> response3 = adService.getAdsWithCategoryAndFilter(filterListOfAds);
            List<AdDto> result3 = getAdsWithCategoryAndFilter(filterListOfAds);

            // Add distance to each adDto
            adDto1.setDistance(calculateDistance(lat, lng, ad1.getLat(), ad1.getLng()));
            adDto2.setDistance(calculateDistance(lat, lng, ad2.getLat(), ad2.getLng()));
            adDto3.setDistance(calculateDistance(lat, lng, ad3.getLat(), ad3.getLng()));
            adDto4.setDistance(calculateDistance(lat, lng, ad4.getLat(), ad4.getLng()));

            // Retrieve the distances saved for each ad
            double distance1 = adDto1.getDistance();
            double distance2 = adDto2.getDistance();
            double distance3 = adDto3.getDistance();
            double distance4 = adDto4.getDistance();

            // Load the distances into an arrayList
            ArrayList<Double> distances = new ArrayList<>();
            distances.add(distance1);
            distances.add(distance2);
            distances.add(distance3);
            distances.add(distance4);

            // TODO where in the method do we retrieve the distance ? from dto or object
            int counter = 0;

            for (Double distance : distances) {
                if (distance >= lowerDistance && distance <= upperDistance) {
                    counter++;
                }
            }

            assertEquals(HttpStatus.OK.value(), response3.getStatusCodeValue());
            assertEquals(result3.size(), counter);

            // Filter ads on distance and category
            filterListOfAds.setCategory(category); // todo use "" if null gives error
            ResponseEntity<Object> response4 = adService.getAdsWithCategoryAndFilter(filterListOfAds);
            List<AdDto> result4 = getAdsWithCategoryAndFilter(filterListOfAds);

            // For all the ads in the category, get those with distance > 9 and < 1000 (3 of the ads)
            assertEquals(HttpStatus.OK.value(), response4.getStatusCodeValue());
            assertEquals(result4.size(), 3);
        }

        /**
         * ---------------------------------------- ABOUT BELOW CODE: ----------------------------------------
         *
         * Code contains only copied methods from "AdServiceImpl.java" (for testing purposes). The purpose is not having
         * to deal with response-entities when testing. Methods for testing sorting therefore contains both: - The
         * original service-methods for checking correctly received HTTP-responses. - These methods in order to verify
         * the correctness of the body of the responses received.
         **/

        private List<AdDto> getAllAdsWithDistance(UserGeoLocation userGeoLocation) {
            ArrayList<AdDto> ads = new ArrayList<>();

            for (Ad ad : adRepository.findAll()) {

                // Setting all attributes and decompressing pictures in help method
                AdDto adDto = castObject(ad);

                // Calculate and set distance
                adDto.setDistance(calculateDistance(userGeoLocation.getLat(), userGeoLocation.getLng(), ad.getLat(),
                        ad.getLng()));

                // Adding all ads to list and then response
                ads.add(adDto);
            }
            return ads.stream().sorted(Comparator.comparing(AdDto::getDistance)).collect(Collectors.toList());
        }

        public List<AdDto> getAdsWithCategoryAndFilter(FilterListOfAds filterListOfAds) {
            UserGeoLocation userGeoLocation = new UserGeoLocation(filterListOfAds.getLat(), filterListOfAds.getLng());

            // If there is a category we sort for it
            if (filterListOfAds.getCategory() != null) {
                List<AdDto> list = getAllAdsInCategoryAndSubCategories(filterListOfAds.getCategory(), userGeoLocation);
                filterListOfAds.setList(list);
                return getAllAdsWithFilter(filterListOfAds);
            }
            // If there is no category we do not sort for it
            else {
                return getAllAdsWithFilter(filterListOfAds);
            }
        }

        public List<AdDto> getAllAdsWithFilter(FilterListOfAds filterListOfAds) {
            List<Ad> ads = new ArrayList<>();
            if (filterListOfAds.getList() != null) {
                for (AdDto a : filterListOfAds.getList()) {
                    ads.add(adRepository.findById(a.getAdId()).get());
                }
            } else {
                ads = adRepository.findAll();
            }
            List<AdDto> list = new ArrayList<>();
            if (filterListOfAds.getFilterType().toLowerCase().equals("distance")) {
                list = ads.stream().map(ad -> modelMapper.map(ad, AdDto.class)).collect(Collectors.toList());
            }

            if (filterListOfAds.getFilterType().toLowerCase().equals("price")) {

                for (Ad a : ads) {
                    if (a.getPrice() < filterListOfAds.getUpperLimit()
                            && a.getPrice() > filterListOfAds.getLowerLimit()) {

                        list.add(modelMapper.map(a, AdDto.class));
                    }
                }
            }
            // Returning array with nearest location
            if (filterListOfAds.getLat() != 0 && filterListOfAds.getLng() != 0) {
                for (AdDto a : list) {
                    a.setDistance(calculateDistance(filterListOfAds.getLat(), filterListOfAds.getLng(), a.getLat(),
                            a.getLng()));
                }
                // setting them in the right order
                if (filterListOfAds.isLowestValueFirst()) {
                    list.sort(Comparator.comparing(AdDto::getDistance));
                } else {
                    list.sort(Comparator.comparing(AdDto::getDistance).reversed());
                }
                // excluding those that are outside the limit of distance
                if (filterListOfAds.getUpperLimit() != 0
                        && filterListOfAds.getFilterType().toLowerCase().equals("distance")) {
                    list.removeIf(a -> a.getDistance() > filterListOfAds.getUpperLimit());
                }
                return list;
            }

            return null;
        }

        public ArrayList<AdDto> getAllAdsInCategoryAndSubCategories(String name, UserGeoLocation userGeoLocation) {

            // Retrieve all categories from database
            ArrayList<Category> categories = (ArrayList<Category>) categoryRepository.findAll();

            // List of subCategories found using recursive function
            List<Category> subCategories = findSubCategories(categories, new ArrayList<>(), name, 0);

            ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

            // Iterate over all sub-categories found
            for (Category category : subCategories) {
                // Iterate over all ads in category
                if (category.getAds() != null) {
                    for (Ad ad : category.getAds()) {
                        // Create dto
                        AdDto dto = castObject(ad);
                        // Add to list
                        adsToBeReturned.add(dto);
                    }
                }
            }

            // Find the parent category
            Set<Category> parentCategories = categoryRepository.findByName(name);

            // There should only be ONE category in the set
            Category parentCategory = parentCategories.stream().findFirst().get();

            // Now, also add the ads connected to only the parent category to the list!
            if (parentCategory.getAds() != null) {
                for (Ad ad : parentCategory.getAds()) {
                    AdDto dto = castObject(ad);
                    adsToBeReturned.add(dto);
                }
            }
            // Calculation and setting distance for ads
            for (AdDto a : adsToBeReturned) {
                a.setDistance(
                        calculateDistance(userGeoLocation.getLat(), userGeoLocation.getLng(), a.getLat(), a.getLng()));
            }
            // sort so nearest ads comes first
            adsToBeReturned.sort(Comparator.comparing(AdDto::getDistance));

            // Now all ads are returned
            return adsToBeReturned;
        }

        private List<Category> findSubCategories(ArrayList<Category> listIn, ArrayList<Category> listOut,
                String parentName, int start) {

            // Position in array == start
            int arrayLength = start;

            // Base case: If the position in the array is equal to the size of the array
            if (arrayLength == listIn.size()) {
                // Return the list that now contains all sub-categories
                return listOut;
            } else {
                // Iterate through all categories
                for (int i = start; i < listIn.size(); i++) {
                    Category category = listIn.get(i);

                    // If the category is a sub-class
                    if (category.getParentName() != null) {

                        // If a category has current category as parent category
                        if (category.getParentName().equalsIgnoreCase(parentName)) {

                            // Add the category to the list to be returned
                            listOut.add(category);

                            // This category is now the new parent
                            parentName = category.getName();

                            // Call on the function recursively from the start for this category
                            findSubCategories(listIn, listOut, parentName, start);
                        }
                    }
                }
                // Increment the list and call on the function recursively
                return findSubCategories(listIn, listOut, parentName, start + 1);
            }
        }

        private AdDto castObject(Ad ad) {
            AdDto adDto = modelMapper.map(ad, AdDto.class);
            ;

            // decompressing and converting images in support method
            // convertPictures(ad, adDto);
            return adDto;
        }

        private double calculateDistance(double lat1, double long1, double lat2, double long2) {
            double dist = org.apache.lucene.util.SloppyMath.haversinMeters(lat1, long1, lat2, long2);
            return dist / 1000;
        }
    }
}
