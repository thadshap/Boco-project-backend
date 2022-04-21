package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AdRepositoryTest {

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {

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

        // Building an ad --> id is automatically created upon persisting
        Ad ad = Ad.builder().
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

        entityManager.persist(category);
        entityManager.persist(ad);
        entityManager.persist(user);
    }

    @Test
    void whenFindById_thenReturnAd() {
        Ad ad = adRepository.findById(1L).get();
        assertEquals(ad.getTitle(), "Shoes");
    }

    @Test
    void getAllAvailableAds() {
    }

    @Test
    void getAvailableAdsByUserId() {
    }

    @Test
    void getReviewsByUserId() {
    }

    @Test
    void findByPostalCode() {
    }

    @Test
    void findByRental() {
    }
}
