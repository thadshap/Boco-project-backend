package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.CategoryDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.user.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.service.user.UserService;
import com.example.idatt2106_2022_05_backend.util.Response;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CategoryIntegrationTest {

    @Autowired
    AdService adService;

    @Autowired
    AdRepository adRepository;

    @Autowired
    UserService userService;

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

    @Test
    public void categoryCreated_WhenCorrectParams() {
        Category category = Category.builder().name("Flasks").parent(true).child(false).build();

        Category categorySaved = categoryRepository.save(category);
        assertNotNull(categorySaved);
        assertNotEquals(category.getId(), 0);
    }

    /**
     * Class contains methods related to categories (from service), but return the proper objects instead of responses
     * (easier to test)
     */
    @Nested
    class CategoryMethods {
        private ModelMapper modelMapper = new ModelMapper();


        @Test
        public void getAllParentCategoriesTest() {
            // Using copied over method
            List<CategoryDto> parentsAtStart = getAllParentCategories();

            // Creating parent categories
            Category category1 = Category.builder().name("Flasks").parent(true).child(false).build();
            Category category2 = Category.builder().name("Action figures2").parent(true).child(false).build();

            // Persist
            categoryRepository.save(category1);
            categoryRepository.save(category2);

            // Using copied over method
            List<CategoryDto> parentsAtEnd = getAllParentCategories();
            assertNotEquals(parentsAtStart, parentsAtEnd);

            // Testing service method as well
            ResponseEntity<Object> response = adService.getAllParentCategories();
            assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        }

        @Test
        public void getAllSubCategories() {
            // Creating parent categories
            Category category1 = Category.builder().name("Flasks2").parent(true).child(false).build();

            // Creating sub categories
            Category category2 = Category.builder().name("Drink flasks").parentName("Flasks2").parent(false).child(true)
                    .build();
            Category category3 = Category.builder().name("Action figures").parentName("Flasks2").parent(true)
                    .child(true).build();

            // Creating sub categories for sub category
            Category category4 = Category.builder().name("Marvel").parentName("Action figures2").parent(false)
                    .child(true).build();

            // Persist
            categoryRepository.save(category1);
            categoryRepository.save(category2);
            categoryRepository.save(category3);
            categoryRepository.save(category4);

            // Get lvl1 subs
            List<CategoryDto> subCategoriesLvl1 = getAllSubCategories("Flasks2");

            // Get lvl2 subs
            List<CategoryDto> subCategoriesLvl2 = getAllSubCategories("Action figures2");

            assertEquals(2, subCategoriesLvl1.size());
            assertEquals(1, subCategoriesLvl2.size());

            // Testing service method as well
            ResponseEntity<Object> response = adService.getAllSubCategories("Flasks2");
            assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        }

        @SneakyThrows
        @Test
        public void getAllAdsForCategoryAndSubCategories() {
            // Create new user
            User userCreation = User.builder().firstName("firstName").lastName("lastName").email("karoline.wahls@hotmail.com")
                    .password("pass1word").build();

            // Saving the user
            User user = userRepository.save(userCreation);

            // Creating parent categories
            Category category1 = Category.builder().name("Flasks and SUCH").level(1).parent(true).child(false).build();

            // Two sub categories
            Category category2 = Category.builder().name("Action figures").level(2).parentName("Flasks and SUCH").parent(false).child(true)
                    .build();

            Category category3 = Category.builder().name("Marvel").level(2).parentName("Flasks and SUCH").parent(true)
                    .child(true).build();

            // Creating sub categories for sub category
            Category category4 = Category.builder().name("Marvel sub category").level(3).parentName("Marvel").parent(false)
                    .child(true).build();

            // Persist
            Category category1Saved = categoryRepository.save(category1);
            Category category2Saved = categoryRepository.save(category2);
            Category category3Saved = categoryRepository.save(category3);
            Category category4Saved = categoryRepository.save(category4);


            // Create ad for parent
            AdDto ad1 = AdDto.builder().title("Flask the best one")
                    .description("Renting out best flask ever without specific category....").rental(true)
                    .rentedOut(false).durationType(AdType.WEEK).price(100).
                    streetAddress("Sagbakken 2").postalCode(7234).city("Ler").
                    userId(user.getId()).categoryId(category1Saved.getId()).build();

            // Create ad for child layer 1
            AdDto ad2 = AdDto.builder().title("Drink flasks").description("Renting out drink flask").rental(true)
                    .rentedOut(false).durationType(AdType.WEEK).price(100).streetAddress("Sagbakken 2")
                    .postalCode(7234).city("Ler").userId(user.getId()).categoryId(category2Saved.getId()).build();

            // Ad for the 2nd layer child
            AdDto ad3 = AdDto.builder().title("Metal flask").description("Renting out metal flask").rental(true)
                    .rentedOut(false).durationType(AdType.WEEK).price(100).streetAddress("Sagbakken 2")
                    .postalCode(7234).city("Ler").userId(user.getId()).categoryId(category3Saved.getId()).build();

            // Ad for the 2nd layer child
            AdDto ad4 = AdDto.builder().title("Metal flask").description("Renting out metal flask").rental(true)
                    .rentedOut(false).durationType(AdType.WEEK).price(100).streetAddress("Sagbakken 2")
                    .postalCode(7234).city("Ler").userId(user.getId()).categoryId(category4Saved.getId()).build();

            // Persist the ads
            adService.postNewAd(ad1);
            adService.postNewAd(ad2);
            adService.postNewAd(ad3);
            adService.postNewAd(ad4);


            UserGeoLocation dto = new UserGeoLocation();
            dto.setLat(63.09567);
            dto.setLng(10.14321);

            // Get all 3 ads by calling the parent category once
            ArrayList<AdDto> result = getAllAdsInCategoryAndSubCategories("Flasks and SUCH", dto);

            // Assertion
            assertEquals(4, result.size());

            // Do the same thing using service
            ResponseEntity<Object> response = adService.getAllAdsInCategoryAndSubCategories("Flasks and SUCH", dto);

            // Assertion
            assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        }

        @SneakyThrows
        @Test
        public void getAllAdsForSpecificCategory() {
            // Create a user
            User userCreation = User.builder().firstName("firstName").lastName("lastName").
                    email("karoline.wahls@hotmail.com").
                    password("pass1word").
                    build();

            // Saving the user
            User user = userRepository.save(userCreation);

            assertNotNull(user);
            // Create category
            Category category = Category.builder().name("Flasks1").parent(true).child(false).build();

            // Persist
            Category categorySaved = categoryRepository.save(category);

            // Create ad for parent
            AdDto ad1 = AdDto.builder().title("Flask the best one")
                    .description("Renting out best flask ever without specific category....").rental(true)
                    .rentedOut(false).durationType(AdType.WEEK).price(100).streetAddress("Project Road 4")
                    .postalCode(7234).userId(user.getId()).categoryId(categorySaved.getId()).build();

            // Create ad for child layer 1
            AdDto ad2 = AdDto.builder().title("Drink flasks").description("Renting out drink flask").rental(true)
                    .rentedOut(false).durationType(AdType.WEEK).price(100).streetAddress("Project Road 4")
                    .postalCode(7234).userId(user.getId()).categoryId(categorySaved.getId()).build();

            // Ad for the 2nd layer child
            AdDto ad3 = AdDto.builder().title("Metal flask").description("Renting out metal flask").rental(true)
                    .rentedOut(false).durationType(AdType.WEEK).price(100).streetAddress("Project Road 4")
                    .postalCode(7234).userId(user.getId()).categoryId(categorySaved.getId()).build();

            // Persist the ads
            adService.postNewAd(ad1);
            adService.postNewAd(ad2);
            adService.postNewAd(ad3);

            // Use the method
            List<AdDto> ads = getAllAdsInCategory("Flasks1");

            assertEquals(ads.size(), 3);

            // Testing service method as well
            ResponseEntity<Object> response = adService.getAllAdsInCategory("Flasks1");
            assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        }

        /********************** Code from ad service related to methods (for testing) ***********************/

        public ArrayList<AdDto> getAllAdsInCategoryAndSubCategories(String name, UserGeoLocation userGeoLocation) {

            // Retrieve ParentCategory
            Set<Category> categoryFound = categoryRepository.findByName(name);
            Category category = categoryFound.stream().findFirst().get();

            // List of subCategories found using recursive function
            List<Category> subCategories = adService.findSubCategories(category.getId());
            ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

            if(subCategories != null) {
                // Iterate over all sub-categories found
                for (Category category1 : subCategories) {
                    // Iterate over all ads in category
                    if (category1.getAds() != null) {
                        for (Ad ad : category1.getAds()) {
                            try {
                                // Create dto
                                AdDto dto = castObject(ad);
                                // Add to list
                                adsToBeReturned.add(dto);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            // Find the parent category
            Set<Category> parentCategories = categoryRepository.findByName(name);

            // There should only be ONE category in the set
            Category parentCategory = parentCategories.stream().findFirst().get();

            // Now, also add the ads connected to only the parent category to the list!
            if(parentCategory.getAds() != null) {
                for (Ad ad : parentCategory.getAds()) {
                    AdDto dto = null;
                    try {
                        dto = castObject(ad);
                        adsToBeReturned.add(dto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Calculation and setting distance for ads
            for (AdDto a : adsToBeReturned) {
                a.setDistance(
                        calculateDistance(userGeoLocation.getLat(), userGeoLocation.getLng(), a.getLat(), a.getLng()));
            }
            if (adsToBeReturned.size() > 0) {
                // sort so nearest ads comes first
                adsToBeReturned.sort(Comparator.comparing(AdDto::getDistance));

                // Now all ads are returned
                return adsToBeReturned;
            } else {
                return null;
            }
        }

        public double calculateDistance(double lat1, double long1, double lat2, double long2) {
            double dist = org.apache.lucene.util.SloppyMath.haversinMeters(lat1, long1, lat2, long2);
            return dist / 1000;
        }

        public List<AdDto> getAllAdsInCategory(String name) {
            Set<Category> categories = categoryRepository.findByName(name);

            List<AdDto> adsToReturn = new ArrayList<>();

            // If category exists
            if (categories != null) {
                for (Category category : categories) {
                    // Get all ads in category
                    Set<Ad> adsFound = category.getAds();
                    // If there are any ads in category
                    if (adsFound != null) {
                        for (Ad ad : adsFound) {
                            try {
                                AdDto newDto = castObject(ad);
                                newDto.setLat(ad.getLat());
                                newDto.setLng(ad.getLng());
                                adsToReturn.add(newDto);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                // Return the ads
                return adsToReturn;
            } else {
                return null;
            }
        }

        public List<CategoryDto> getAllSubCategories(String parentName) {
            // List that will be returned
            ArrayList<CategoryDto> subCategories = new ArrayList<>();

            // Retrieve all categories from database
            List<Category> categories = categoryRepository.findAll();

            // Iterate over all categories
            for (Category category : categories) {

                // Ensure null-safety by skipping the category if it does not have a parent
                if (category.getParentName() != null) {

                    // Using equals w/ignore case() to ensure equality
                    if (parentName.equalsIgnoreCase(category.getParentName())) {

                        // Generate a new list that holds only the ids --> avoids recursive stackOverflow
                        ArrayList<Long> ids = new ArrayList<>();

                        // If this category has any ads
                        if (category.getAds().size() > 0) {
                            for (Ad ad : category.getAds()) {
                                ids.add(ad.getId());
                            }
                        }

                        // Create dto
                        CategoryDto dto = CategoryDto.builder().id(category.getId()).name(category.getName())
                                .parentName(parentName).parent(category.isParent()).child(category.isParent())
                                .adIds(ids).build();

                        // Add to list of sub-categories to return
                        subCategories.add(dto);
                    }
                }
            }

            // Return the list if any subcategories were added
            if (subCategories.size() > 0) {
                return subCategories;
            }
            // Return NOT_FOUND if there
            else {
                return null;
            }
        }

        private AdDto castObject(Ad ad) throws IOException {
            AdDto adDto = modelMapper.map(ad, AdDto.class);
            ;

            // decompressing and converting images in support method
            // convertPictures(ad, adDto);
            return adDto;
        }

        public List<CategoryDto> getAllParentCategories() {
            List<Category> allCategories = categoryRepository.findAll();
            List<CategoryDto> categoriesToReturn = new ArrayList<>();

            for (Category category : allCategories) {
                if (category.isParent()) {
                    CategoryDto dto = CategoryDto.builder().id(category.getId()).name(category.getName()).build();
                    categoriesToReturn.add(dto);
                }
            }
            if (categoriesToReturn.size() > 0) {
                // Return all the DTOs
                return categoriesToReturn;
            } else {
                return null;
            }
        }
    }
}
