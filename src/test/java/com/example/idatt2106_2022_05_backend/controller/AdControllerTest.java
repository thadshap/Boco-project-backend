package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Review;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdController.class)
class AdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdService adService;

    // Creating an uninitialized Ad-object
    private Ad ad;
    private User user;

    private Set<Ad> ads;


    @BeforeEach
    void setUp() {

        // Initializing ad for use in our test-cases
        ad = Ad.builder().
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

        // Building a user
        user = User.builder().
                id(2L).
                firstName("firstName").
                lastName("lastName").
                email("user.name@hotmail.com").
                password("pass1word").
                build();

        // Set the foreign key for the ad
        ad.setUser(user);

        // Add the new ad to the list of ads
        ads.add(ad);

        // Add the list of ads to the user
        user.setAds(ads);
    }

    @Test
    void getAdById() throws Exception {

        // Mocking that we retrieve an element from the repository (we are after all not testing repo now)
        Mockito.when(adService.getAdById(1L)).
                thenReturn(new Response(ad,HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(get("/ads/1").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.title").value(ad.getTitle()));
    }

    @Test
    void getAllAds() throws Exception {
        Mockito.when(adService.getAllAds()).
                thenReturn(new Response(ads,HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(get("/ads").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());
    }

    @Test
    void getAllAvailableAds() throws Exception {
        Mockito.when(adService.getAllAvailableAds()).
                thenReturn(new Response(ads,HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(get("/ads/available").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()); //todo test that size = 1 ?
    }

    @Test
    void getAvailableAdsByUserId() throws Exception {
        Mockito.when(adService.getAllAvailableAdsByUser(2L)).
                thenReturn(new Response(ads,HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(get("/ads/available/2").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()); //todo test that size = 1
    }

    @Test
    void getAdByPostalCode() throws Exception {
        Mockito.when(adService.getAllAdsByPostalCode(7234)).
                thenReturn(new Response(ads,HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(post("/ads").
                        contentType(MediaType.APPLICATION_JSON).
                        content("{\n" +
                                "\t\"postalCode\" : 7234\n" +
                                "}")).
                andExpect(status().isOk());
    }

    @Test
    void getAllAdsByRentalType() throws Exception {
        Mockito.when(adService.getAllAdsByRentalType(true)).
                thenReturn(new Response(ads,HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(post("/ads").
                        contentType(MediaType.APPLICATION_JSON).
                        content("{\n" +
                                "\t\"rentalType\" : 7234\n" +
                                "}")).
                andExpect(status().isOk());
    }

    @Test
    void postAd() throws Exception {
        // Simulating input dto from frontend contain new ad
        AdDto inputAd = AdDto.builder().
                title("Pants").
                description("Renting out a pair of pants in size 36").
                rental(true).
                rentedOut(false).
                durationType(AdType.MONTH).
                duration(2).
                price(100).
                streetAddress("Project Road 4").
                postalCode(7200).
                build();

        Mockito.
                when(adService.postNewAd(inputAd)).
                thenReturn(new Response(null, HttpStatus.OK));

        // Performing the post request of the controller endpoint
        mockMvc.perform(post("/ads/newAd").
                contentType(MediaType.APPLICATION_JSON).
                content("{\n" +
                        "\t\"title\" : \"Pants\",\n" +
                        "\t\"description\" : \"Renting out a pair of pants in size 36\",\n" +
                        "\t\"rental\" : true,\n" +
                        "\t\"rentedOut\" : false,\n" +
                        "\t\"durationType\" : \"MONTH\",\n" +
                        "\t\"duration\" : 2,\n" +
                        "\t\"price\" : 100,\n" +
                        "\t\"streetAddress\" : \"Project Road 4\",\n" +
                        "\t\"postalCode\" : 7200\n" +
                        "}")).andExpect(status().isOk());
    }

    @Test
    void getReviewsByUserId() throws Exception {
        Set<Review> reviews = new HashSet<>();

        Review review = Review.builder().
                id(5L).
                description("Great shoes!").
                rating(5).
                user(user).
                build();

        reviews.add(review);

        Mockito.when(adService.getReviewsByUserId(2L)).
                thenReturn(new Response(reviews,HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(get("users/ads/reviews/2").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());
    }

    @Test
    void updateTitle() throws Exception {
//        Mockito.when(adService.updateTitle(1L, "newTitle")).
//                thenReturn(new Response(null,HttpStatus.OK));

        // Performing the post operation
        mockMvc.perform(post("/ads/updateTitle").
                        contentType(MediaType.APPLICATION_JSON).
                        content("{\n" +
                                "\t\"title\" : \"newTitle\"\n" +
                                "}")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.title").value(ad.getTitle()));;
    }

    @Test
    void updateDescription() throws Exception {
//        Mockito.when(adService.updateDescription(1L, "new description")).
//                thenReturn(new Response(null,HttpStatus.OK));

        // Performing the post operation
        mockMvc.perform(post("/ads/updateDescription").
                        contentType(MediaType.APPLICATION_JSON).
                        content("{\n" +
                                "\t\"description\" : \"new description\"\n" +
                                "}")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.description").value(ad.getDescription()));
    }

    @Test
    void updateDuration() throws Exception {
//        Mockito.when(adService.updateDuration(1L, 10)).
//                thenReturn(new Response(null,HttpStatus.OK));

        // Performing the post operation
        mockMvc.perform(post("/ads/updateDuration").
                        contentType(MediaType.APPLICATION_JSON).
                        content("{\n" +
                                "\t\"duration\" : 10\n" +
                                "}")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.duration").value(ad.getDuration()));
    }

    @Test
    void updateDurationType() throws Exception {
//        Mockito.when(adService.updateDurationType(1L, AdType.DAY)).
//                thenReturn(new Response(null,HttpStatus.OK));

        // Performing the post operation
        mockMvc.perform(post("/ads/updateDurationType").
                        contentType(MediaType.APPLICATION_JSON).
                        content("{\n" +
                                "\t\"durationType\" : DAY\n" +
                                "}")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.durationType").
                        value(ad.getDurationType()));    }

    @Test
    void updatePrice() throws Exception {
//        Mockito.when(adService.updatePrice(1L, 300)).
//                thenReturn(new Response(null,HttpStatus.OK));

        // Performing the post operation
        mockMvc.perform(post("/ads/updatePrice").
                        contentType(MediaType.APPLICATION_JSON).
                        content("{\n" +
                                "\t\"price\" : 300\n" +
                                "}")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.price").value(ad.getPrice()));
    }

    @Test
    void updateStreetAddress() throws Exception {
//        Mockito.when(adService.updateStreetAddress(1L, "new address 4")).
//                thenReturn(new Response(null,HttpStatus.OK));

        // Performing the post operation
        mockMvc.perform(post("/ads/updateStreetAddress").
                        contentType(MediaType.APPLICATION_JSON).
                        content("{\n" +
                                "\t\"streetAddress\" : \"new address 4\"\n" +
                                "}")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.streetAddress").
                        value(ad.getStreetAddress()));
    }

    @Test
    void updatePostalCode() throws Exception {
//        Mockito.when(adService.updatePostalCode(1L, 1111)).
//                thenReturn(new Response(null,HttpStatus.OK));

        // Performing the post operation
        mockMvc.perform(post("/ads/updatePostalCode").
                        contentType(MediaType.APPLICATION_JSON).
                        content("{\n" +
                                "\t\"postalCode\" : 1111\n" +
                                "}")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.postalCode").
                        value(ad.getPostalCode()));
    }

    @Test
    void updateRentedOut() throws Exception {
//        Mockito.when(adService.updateRentedOut(1L, true)).
//                thenReturn(new Response(null,HttpStatus.OK));

        // Performing the post operation
        mockMvc.perform(post("/ads/updateRentedOut").
                        contentType(MediaType.APPLICATION_JSON).
                        content("{\n" +
                                "\t\"rentedOut\" : true\n" +
                                "}")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.rentedOut").value(ad.isRentedOut()));
    }

    @Test
    void deleteAd() {

    }
}
