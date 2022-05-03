package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
@SpringBootTest(classes = { Ad.class })
@ExtendWith(SpringExtension.class)
class AdServiceTest {

    @MockBean
    private AdService adService;

    // @Autowired
    // AdRepository adRepository;

    @MockBean
    private AdRepository adRepository;

    /**
     * Creating an Ad object for use in each test (mocking repository) Ad is created using the Builder-pattern (Lombok),
     * which allows us to create all possible variants of constructors for the object. To create an ad, a user and a
     * category must also be created.
     *
    @BeforeEach
    void setUp() { // description, rental (boolean), rented_out,
                   // duration_type, duration, price, street_address, postal_code
                   // picture, title, longitude, latitude, category_id (fk), userId (fk)

        // Set of ads
        Set<Ad> ads = new HashSet<>();

        // Building an ad
        Ad ad = Ad.builder().id(1L).title("Shoes").description("Renting out a pair of shoes in size 36").rental(true)
                .rentedOut(false).durationType(AdType.WEEK).duration(2).price(100).streetAddress("Project Road 4")
                .postalCode(7234).build();

        // Building a user
        User user = User.builder().id(2L).firstName("firstName").lastName("lastName").email("user.name@hotmail.com")
                .password("pass1word").build();

        // Building a category
        Category category = Category.builder().id(3L).name("Shoes").build();

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
        Mockito.when(adRepository.findById(1L)).thenReturn(Optional.of(ad));
        Mockito.when(adRepository.getAvailableAdsByUserId(2L)).thenReturn(Collections.singleton((ad)));
    }

    @Test
    void getPageOfAds() throws IOException, InterruptedException {
        User user = User.builder().id(1L).firstName("firstName").lastName("lastName").email("user.name@hotmail.com")
                .password("pass1word").build();

        Category category = new Category();
        category.setName("Kategori1");
        category.setId((long) 1);

        for (int i = 0; i < 20; i++) {
            AdDto ad = new AdDto();
            ad.setDescription("text:" + i);
            ad.setDuration(i);
            ad.setDurationType(AdType.HOUR);
            ad.setPostalCode(1234 + i);
            ad.setPrice(10 * i);
            ad.setRental(false);
            ad.setRentedOut(false);
            ad.setCategoryId(1);
            ad.setStreetAddress("Olavsgate" + i);
            ad.setTitle("Ad" + i);
            adService.postNewAd(ad);
        }
        Pageable pageOf24 = PageRequest.of(0, 25);
        List<Ad> ads = adRepository.findAll(pageOf24).getContent();
        System.out.println("ads:" + ads);
    }

    /**
     * This test uses the AdRepository method "findByPostalCode". This repository method is mocked
     *
    @Test
    void getAllAdsByPostalCode() {

        // Postal code that exists in db
        int postalCode = 7234;
        Set<Ad> foundAds = (Set<Ad>) adService.getAllAdsByPostalCode(postalCode).getBody();

        assert foundAds != null;
        for (Ad ad : foundAds) {
            if (ad.getPostalCode() == postalCode) {
                assertEquals(postalCode, ad.getPostalCode());
            }
        }
    }

    @Test
    void getAdById() {
        // Id that exists in db
        Long id = 1L;
        Ad ad = (Ad) adService.getAdById(id).getBody();

        assertTrue(ad.getTitle().equalsIgnoreCase("Shoes"));
    }

    @Test
    void getAllAvailableAdsByUser() {
        // Id that exists in db
        Long id = 2L;
        Set<Ad> availableAds = (Set<Ad>) adService.getAllAvailableAdsByUser(id).getBody();
        assert availableAds != null;
        assertEquals(1, availableAds.size());
    }

    @Test
    void getAllAdsByRentalType() {
        // Id that exists in db
        boolean rentalType = true;
        Set<Ad> rentalTrueAds = (Set<Ad>) adService.getAllAdsByRentalType(true).getBody();
        assert rentalTrueAds != null;
        assertEquals(1, rentalTrueAds.size());
    }

    @Test
    void newAdIsPosted() {

    }

    @Test
    void getReviewsByUserId() {

    }

    @Test
    void adIsUpdated() {

    }
}
*/
