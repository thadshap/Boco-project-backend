package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
     * us to create all possible variants of constructors for the object
     */
    @BeforeEach
    void setUp() { // description, rental (boolean), rented_out,
                   // duration_type, duration, price, street_address, postal_code
                   // picture, title, longitude, latitude, category_id (fk), user_id (fk)
        Ad ad = Ad.builder().
                title("Shoes").


    }

    @Test
    void whenAdsExist_thenGetAllAds() {
    }

    @Test
    void getAdById() {
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
