package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdServiceTest {

    @Autowired
    private AdService adService;

    @MockBean
    private AdRepository adRepository;

    /**
     * Creating an Ad object for use in each test (mocking repository)
     * Ad is created using the Builder-pattern (Lombok), which allows
     * us to create all possible variants of constructors for the object.
     * To create an ad, a user and a category must also be created.
     */
    @BeforeEach
    void setUp() { // description, rental (boolean), rented_out,
                   // duration_type, duration, price, street_address, postal_code
                   // picture, title, longitude, latitude, category_id (fk), user_id (fk)

        // Set of ads
        Set<Ad> ads = new HashSet<>();

        // Building a user
        User user = User.builder().
                id(1L).
                firstName("firstName").
                lastName("lastName").
                email("user.name@hotmail.com").
                password("pass1word").
                build();

        // Building a category
        Category category = Category.builder().
                categoryId(3L).
                name("Shoes").
                build();

        // Building an ad
        Ad ad = Ad.builder().
                adId(1L).
                title("Shoes").
                description("Renting out a pair of shoes in size 36").
                rental(true).
                rentedOut(false).
                durationType(AdType.WEEK).
                duration(2).
                price(100).
                streetAddress("Project Road 4").
                postalCode(7234).
                build();

        // Set the foreign keys for the ad
        ad.setCategory(category);
        ad.setUser(user);

        // Add the new ad to the list of ads
        ads.add(ad);

        // Add the list of ads to the user
        user.setAds(ads);

        // Add the list of ads to the category
        category.setAds(ads);

        // We want to use this ad-object when we call on methods later on.
        Mockito.when(adRepository.findByPostalCode(7234)).thenReturn(ads);
    }


    @Test
    void whenAdsExist_thenGetAllAds() {
    }

    @Test
    void getAdById() {
    }

    @Test
    void getPageOfAds(){
        User user = User.builder().
                id(1L).
                firstName("firstName").
                lastName("lastName").
                email("user.name@hotmail.com").
                password("pass1word").
                build();

        for(int i=0; i<40; i++){
            Ad ad = new Ad();
            ad.setCategory(new Category());
            ad.setDescription("text:" + i);
            ad.setDuration(i);
            ad.setDurationType(AdType.HOUR);
            ad.setLat(63.424595+i);
            ad.setLng(10.810314+i);
            ad.setPictures(null);
            ad.setPostalCode(1234+i);
            ad.setPrice(10*i);
            ad.setRental(false);
            ad.setReviews(null);
            ad.setRentedOut(false);
            ad.setStreetAddress("Olavsgate" + i);
            ad.setTitle("Ad" + i);
            ad.setUser(user);
            adRepository.save(ad);
        }
        Pageable pageOf24 = PageRequest.of(0,25);
        List<Ad> ads = adRepository.findAll(pageOf24).getContent();
        System.out.println("ads:" +ads);

    }

    @Test
    void getAllAvailableAds() {
    }

    @Test
    void getAllAvailableAdsByUser() {
    }

    /**
     * This test uses the AdRepository method "findByPostalCode".
     * This repository method is mocked
     */
    @Test
    void getAllAdsByPostalCode() {

        // Mocking repository call


        // Postal code that exists in db
        int postalCode = 1234;
        Set<Ad> foundAds = (Set<Ad>) adService.getAllAdsByPostalCode(postalCode).getBody();

        for(Ad ad : foundAds) {
            if(ad.getPostalCode() == postalCode) {
                assertEquals(postalCode, ad.getPostalCode());
            }
        }
    }


    void whenAdWithPostalCodeDoesNotExist_returnTrue() {

        // Postal code that does not exist in db
        int postalCode = 1235;
        Set<Ad> foundAds = (Set<Ad>) adService.getAllAdsByPostalCode(postalCode).getBody();

        boolean equalAdFound = false;
        for(Ad ad : foundAds) {
            if(ad.getPostalCode() == postalCode) {
                equalAdFound = true;
            }
        }

        // If equalAdFound --> test failed
        assertFalse(equalAdFound);
    }

    @Test
    void getAllAdsByRentalType() {
    }

    @Test
    void postNewAd() {
    }

    @Test
    void updateTitle() {
    }

    @Test
    void updateDescription() {
    }

    @Test
    void updateDuration() {
    }

    @Test
    void updateDurationType() {
    }

    @Test
    void updatePrice() {
    }

    @Test
    void deleteAd() {
    }
}
