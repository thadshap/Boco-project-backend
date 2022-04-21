package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.AdDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
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
    void getAllAds() {
    }

    @Test
    void getAllAvailableAds() {
    }

    @Test
    void getAvailableAdsByUserId() {
    }

    @Test
    void getAdByPostalCode() {
    }

    @Test
    void getAllAdsByRentalType() {
    }

    @Test
    void getAdByUserId() {
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
    void getReviewsByUserId() {
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
    void updateStreetAddress() {
    }

    @Test
    void updatePostalCode() {
    }

    @Test
    void updateRentedOut() {
    }

    @Test
    void deleteAd() {
    }
}
