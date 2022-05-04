package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.Review;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CategoryRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdControllerTest {

    private final String requestMapping = "/api/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockBean
    private AdService adService;

    // Creating an uninitialized Ad-object
    private Ad ad;
    private User user;
    private Category category;

    private Set<Ad> ads;

    @BeforeEach
    void setUp() {

        // Initializing ad for use in our test-cases
        ad = Ad.builder().id(1L).title("Shoes").description("Renting out a pair of shoes in size 36").rental(true)
                .durationType(AdType.WEEK).duration(2).price(100).streetAddress("Project Road 4").postalCode(7234)
                .build();

        // Building a user
        user = User.builder().id(2L).firstName("firstName").lastName("lastName").email("user.name@hotmail.com")
                .password("pass1word").build();

        // Persist add
        userRepository.save(user);

        // Create category
        category = Category.builder().id(3L).name("category").build();

        // Save category
        categoryRepository.save(category);

        // Set the foreign key for the ad
        ad.setUser(user);
        ad.setCategory(category);

        // Save ad
        adRepository.save(ad);

        // Add the new ad to the list of ads
        ads = new HashSet<>();
        ads.add(ad);

        // Add the list of ads to the user
        user.setAds(ads);

        // Save the user
        userRepository.save(user);
    }

    @AfterEach
    public void cleanup() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setAds(null);
        }
        List<Ad> allAds = adRepository.findAll();
        for (Ad ad : allAds) {
            ad.setUser(null);
            ad.setCategory(null);
            adRepository.delete(ad);
        }
        userRepository.deleteAll(users);
        categoryRepository.deleteAll();
    }

    @Test
    void getAdById() throws Exception {

        // Mocking that we retrieve an element from the repository (we are after all not testing repo now)
        Mockito.when(adService.getAdById(1L)).thenReturn(new Response(ad, HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(get("/ads/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(ad.getTitle()));
    }

    @Test
    void getAllAds() throws Exception {
        Mockito.when(adService.getAllAds()).thenReturn(new Response(ads, HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(get("/ads").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void getAllAvailableAds() throws Exception {
        Mockito.when(adService.getAllAvailableAds()).thenReturn(new Response(ads, HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(get("/ads/available").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()); // todo
                                                                                                                   // test
                                                                                                                   // that
                                                                                                                   // size
                                                                                                                   // =
                                                                                                                   // 1
                                                                                                                   // ?
    }

    @Test
    void getAvailableAdsByUserId() throws Exception {
        Mockito.when(adService.getAllAvailableAdsByUser(2L)).thenReturn(new Response(ads, HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(get("/ads/available/2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()); // todo
                                                                                                                     // test
                                                                                                                     // that
                                                                                                                     // size
                                                                                                                     // =
                                                                                                                     // 1
    }

    @Test
    void getAdByPostalCode() throws Exception {
        Mockito.when(adService.getAllAdsByPostalCode(7234)).thenReturn(new Response(ads, HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(
                post("/ads").contentType(MediaType.APPLICATION_JSON).content("{\n" + "\t\"postalCode\" : 7234\n" + "}"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllAdsByRentalType() throws Exception {
        Mockito.when(adService.getAllAdsByRentalType(true)).thenReturn(new Response(ads, HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(
                post("/ads").contentType(MediaType.APPLICATION_JSON).content("{\n" + "\t\"rentalType\" : 7234\n" + "}"))
                .andExpect(status().isOk());
    }

    @WithMockUser(value = "spring")
    @Test
    void postAd() throws Exception {
        Ad ad = Ad.builder().title("Pants").description("Renting out a pair of pants in size 36").rental(true)
                .durationType(AdType.MONTH).duration(2).price(100).streetAddress("Project Road 4").postalCode(7200)
                .user(user).category(category).build();
        adRepository.save(ad);

        // Simulating input dto from frontend contain new ad
        AdDto inputAd = AdDto.builder().title("Pants").description("Renting out a pair of pants in size 37")
                .rental(true).durationType(AdType.MONTH).duration(2).price(100).streetAddress("Project Road 4")
                .postalCode(7200).userId(2).categoryId(3).build();

        this.mockMvc
                .perform(post(requestMapping + "/ads/newAd").with(user("USER")).contentType(MediaType.APPLICATION_JSON)
                        .content("\t\"title\" : \"Pants\",\n"
                                + "\t\"description\" : \"Renting out a pair of pants in size 37\",\n"
                                + "\t\"rental\" : true,\n" + "\t\"durationType\" : \"MONTH\",\n"
                                + "\t\"duration\" : 2,\n" + "\t\"price\" : 100,\n"
                                + "\t\"streetAddress\" : \"Project Road 4\",\n" + "\t\"postalCode\" : 7200,\n"
                                + "\t\"userId\" : 2,\n" + "\t\"categoryId\" : 3"))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @WithMockUser(value = "spring")
    @Test
    void getReviewsByUserId() throws Exception {
        Set<Review> reviews = new HashSet<>();

        Review review = Review.builder().id(5L).description("Great shoes!").rating(5).build();

        reviews.add(review);

        Mockito.when(adService.getReviewsByUserId(2L)).thenReturn(new Response(reviews, HttpStatus.OK));

        // Performing the get operation
        mockMvc.perform(get(requestMapping + "users/ads/reviews/" + 2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser(value = "spring")
    @Test
    void updateTitle() throws Exception {
        this.mockMvc.perform(put(requestMapping + "ads/" + 1).contentType(MediaType.APPLICATION_JSON)
                .content("{\n" + "\t\"title\" : \"newTitle\"\n" + "}")).andExpect(status().isOk());
    }

    @WithMockUser(value = "spring")
    @Test
    void updateDescription() throws Exception {
        this.mockMvc.perform(put(requestMapping + "ads/" + 1).contentType(MediaType.APPLICATION_JSON)
                .content("{\n" + "\t\"description\" : \"newDescription\"\n" + "}")).andExpect(status().isOk());
    }

    @WithMockUser(value = "spring")
    @Test
    void deleteAdReturnsDeleteSuccessMessage() throws Exception {

        this.mockMvc.perform(delete(requestMapping + "ads/" + ad.getId())).andExpect(status().isOk());
    }
    */

