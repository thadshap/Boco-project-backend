package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.CategoryDto;
import com.example.idatt2106_2022_05_backend.dto.UserGeoLocation;
import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

        private List<Category> findSubCategories(ArrayList<Category> listIn, ArrayList<Category> listOut,
                String parentName, int start) {

            // Position in array == start
            int arrayLength = start;

            // Make a counter and if it is not == 1 && base case is not reached when the loop ends,
            // call on the function again from parentName == arrayLength.getName
            int loopCounter = 0;

            // Base case: If the position in the array is equal to the size of the array
            if (arrayLength == listIn.size()) {
                System.out.println("Array length equals list in --> finished");
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
                    System.out.println("parent name is null");
                }
                // Increment the list and call on the function recursively
                return findSubCategories(listIn, listOut, parentName, start + 1);
            }
        }

        public List<AdDto> getAllAdsInCategoryAndSubCategories(String name) {

            // Retrieve all categories from database
            ArrayList<Category> categories = (ArrayList<Category>) categoryRepository.findAll();

            // List of subCategories found using recursive function
            List<Category> subCategories = findSubCategories(categories, new ArrayList<>(), name, 0);

            System.out.println("sub categories found size: " + subCategories.size());

            ArrayList<AdDto> adsToBeReturned = new ArrayList<>();

            // Iterate over all sub-categories found
            for (Category category : subCategories) {
                // Iterate over all ads in category
                if (category.getAds() != null) {
                    for (Ad ad : category.getAds()) {
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
            // Now all ads are returned
            return adsToBeReturned;
        }

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
            Category category1 = Category.builder().name("Flasks").parent(true).child(false).build();

            Category category2 = Category.builder().name("Action figures").parentName("Flasks").parent(true).child(true)
                    .build();

            // Creating sub categories for sub category
            Category category3 = Category.builder().name("Marvel").parentName("Action figures").parent(false)
                    .child(true).build();

            // Persist
            Category category1Saved = categoryRepository.save(category1);
            Category category2Saved = categoryRepository.save(category2);
            Category category3Saved = categoryRepository.save(category3);

            // Create ad for parent
            AdDto ad1 = AdDto.builder().title("Flask the best one")
                    .description("Renting out best flask ever without specific category....").rental(true)
                    .rentedOut(false).durationType(AdType.WEEK).duration(2).price(100).streetAddress("Project Road 4")
                    .postalCode(7234).userId(user.getId()).categoryId(category1Saved.getId()).build();

            // Create ad for child layer 1
            AdDto ad2 = AdDto.builder().title("Drink flasks").description("Renting out drink flask").rental(true)
                    .rentedOut(false).durationType(AdType.WEEK).duration(2).price(100).streetAddress("Project Road 4")
                    .postalCode(7234).userId(user.getId()).categoryId(category2Saved.getId()).build();

            // Ad for the 2nd layer child
            AdDto ad3 = AdDto.builder().title("Metal flask").description("Renting out metal flask").rental(true)
                    .rentedOut(false).durationType(AdType.WEEK).duration(2).price(100).streetAddress("Project Road 4")
                    .postalCode(7234).userId(user.getId()).categoryId(category3Saved.getId()).build();

            // Persist the ads
            adService.postNewAd(ad1);
            adService.postNewAd(ad2);
            adService.postNewAd(ad3);

            // Get all 3 ads by calling the parent category once
            List<AdDto> adsFound = getAllAdsInCategoryAndSubCategories("Flasks");
            assertEquals(adsFound.size(), 3);

            // Testing service method as well
            UserGeoLocation dto = new UserGeoLocation();
            dto.setAmount(1);
            dto.setLng(63.98);
            dto.setLat(63.98);
            ResponseEntity<Object> response = adService.getAllAdsInCategoryAndSubCategories("Flasks", dto);
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
                    .rentedOut(false).durationType(AdType.WEEK).duration(2).price(100).streetAddress("Project Road 4")
                    .postalCode(7234).userId(user.getId()).categoryId(categorySaved.getId()).build();

            // Create ad for child layer 1
            AdDto ad2 = AdDto.builder().title("Drink flasks").description("Renting out drink flask").rental(true)
                    .rentedOut(false).durationType(AdType.WEEK).duration(2).price(100).streetAddress("Project Road 4")
                    .postalCode(7234).userId(user.getId()).categoryId(categorySaved.getId()).build();

            // Ad for the 2nd layer child
            AdDto ad3 = AdDto.builder().title("Metal flask").description("Renting out metal flask").rental(true)
                    .rentedOut(false).durationType(AdType.WEEK).duration(2).price(100).streetAddress("Project Road 4")
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
    }
}
