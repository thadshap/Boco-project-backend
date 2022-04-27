package com.example.idatt2106_2022_05_backend.integration;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.dto.ad.AdUpdateDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CategoryRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.sql.Date;
import java.util.HashSet;
import java.util.NoSuchElementException;

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
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {
        // Building a user
        User user = User.builder().
                firstName("firstName").
                lastName("lastName").
                email("user.name@hotmail.com").
                password("pass1word").
                build();

        // Saving the user
        userRepository.save(user);

        // Building categories
        Category clothes = Category.builder().
                name("Shoes").
                build();

        Category it = Category.builder().
                name("IT").
                build();

        // Saving the categories
        categoryRepository.save(clothes);
        categoryRepository.save(it);
    }

    @AfterEach
    public void emptyDatabase() {
        adRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Nested
    class AdClassTests {
        @Test
        public void whenForeignKeysCorrect_ThenAadIsSaved(){

            // If the setUp @beforeEach works as it should, the repository should be empty
            assertEquals(0, adRepository.findAll().size());

            // Find the user and category created in setUp
            User user = userRepository.findAll().get(0);
            Category clothesCategory = categoryRepository.findAll().get(0);

            // Building an ad-dto with foreign keys
            AdDto ad = AdDto.builder().
                    title("Nike shoes").
                    description("Renting out a pair of shoes in size 40").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.WEEK).
                    duration(2).
                    price(100).
                    streetAddress("Project Road 4").
                    postalCode(7234).
                    userId(user.getId()).
                    categoryId(clothesCategory.getId()).
                    build();

            try {
                // Post the ad
                adService.postNewAd(ad);
            } catch (IOException e) {
                fail();
                e.printStackTrace();
            }

            //Verify that the post is saved
            assertTrue(adRepository.findAll().size() > 0);
            assertEquals(adRepository.findAll().get(0).getTitle(), "Nike shoes");
        }

        @Test
        public void whenForeignKeysWrong_ThenAdIsNotSaved(){
            // Building a user
            User user = User.builder().
                    firstName("user2").
                    lastName("second").
                    email("second.user@hotmail.com").
                    password("newPassword").
                    build();

            // Building a new category
            Category boats = Category.builder().
                    name("Boats").
                    build();

            // Building an ad-dto with foreign keys
            AdDto boatAd = AdDto.builder().
                    title("Sail boat").
                    description("Renting out a huge sail boat").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(100).
                    streetAddress("The sea").
                    postalCode(7000).
                    userId(user.getId()).
                    categoryId(boats.getId()).
                    build();

            try {
                // Post the ad
                adService.postNewAd(boatAd);
                // The test will fail because the foreign keys did not exist
                fail();
            }catch(NoSuchElementException e){
                // todo the test will not receive this exception because the method does not throw it
                // todo cast this exception in adService
                // The test will catch this exception when the foreign keys do not exist
                /**
            }catch(IllegalAccessException e){
                // The test fails if this exception is caught
                fail();
                 */
            } catch (IOException e) {

                // The test fails if this exception is caught
                fail();
                e.printStackTrace();
            }
        }

        @Test
        public void user_in_org_can_add_post_to_org(){
            //Add user to organization
            User user = userRepository.findAll().get(0);
            Organization organization = orgRepository.findAll().get(0);
            UserOrganization userOrg = new UserOrganization(user, organization, OrganizationRole.EMPLOYEE);
            userOrgRepository.save(userOrg);

            //Add post in organization from user
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", user, organization, new HashSet<>());
            try{
                postService.add(post, user);
            }catch(IllegalAccessException | NoSuchElementException e){
                fail();
            }
        }

        @Test
        public void user_out_of_org_cant_add_post_to_org(){
            User user = userRepository.findAll().get(0);
            Organization organization = orgRepository.findAll().get(0);

            //Try to add post in organization from user
            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", user, organization, new HashSet<>());
            try{
                postService.add(post, user);
                fail();
            }catch(IllegalAccessException e){
                //pass test
            }catch(NoSuchElementException e){
                fail();
            }
        }
    }

    @Nested
    class UpdatePostTests{
        @Test
        public void updatePostWorks(){

            // Get the foreign keys
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Building an ad with foreign keys
            Ad newAd = Ad.builder().
                    title("Sail boat").
                    description("Renting out a huge sail boat").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(100).
                    streetAddress("The sea").
                    postalCode(7000).
                    user(user).
                    category(category).
                    build();

            // Persisting the ad and set newAd equal to it in order to fetch the id
            newAd = adRepository.save(newAd);

            // Assert that the ad was persisted
            assertTrue(adRepository.existsById(newAd.getId()));

            // Create an ad update-dto
            AdUpdateDto updateDto = new AdUpdateDto();
            updateDto.setTitle("Renting out sail boat");

            try {
                // Edit the ad
                adService.updateAd(newAd.getId(),updateDto);

                // Get the edited ad and assign it to the ad reference (newAd)
                newAd = adRepository.findById(newAd.getId()).get();

                // Assert that the new ad title was persisted
                assertEquals("Renting out sail boat", newAd.getTitle());

            }catch(NoSuchElementException e){
                fail();
            }
        }

        @Test
        public void nonExistentPostCannotBeUpdated() {
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Building an ad without persisting it
            Ad newAd = Ad.builder().
                    title("non existent post").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("non existent").
                    postalCode(7000).
                    user(user).
                    category(category).
                    build();
            try {
                // Create an update-dto
                AdUpdateDto updateDto = new AdUpdateDto();
                updateDto.setTitle("The ad still does not exist");

                // Trying to edit the ad without having persisted it (the ad does not exist yet)
                adService.updateAd(newAd.getId(), updateDto);

                // The ad-update fails
                fail();
            } catch (NoSuchElementException e) {
                // Pass test if this exception is thrown
            }
        }
    }

    @Nested
    class DeletePostTests{

        @Test
        public void whenPostExists_postIsDeleted(){
            // The cleanup should have erased ads from this repo
            assertEquals(0, adRepository.findAll().size());

            // Retrieve the user and category
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Building an ad without persisting it
            Ad newAd = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            newAd= adRepository.save(newAd);

            // Assert that the ad was saved
            assertEquals(1, adRepository.findAll().size());

            // Delete the newly created ad
            adService.deleteAd(newAd.getId());

            // Assert that the ad was deleted
            assertEquals(0, adRepository.findAll().size());
        }

        @Test
        public void whenPostDoesNotExist_postIsNotDeleted(){
            // The cleanup should have erased ads from this repo
            assertEquals(0, adRepository.findAll().size());

            // Fetch the user
            User user = userRepository.findAll().get(0);

            try {
                // Try to delete an ad (the repo is empty --> this should fail)
                adService.deleteAd(1L);
                fail();
            }
            catch(NoSuchElementException e){
                // Test passed
            }
        }
    }

    @Nested
    class UserRelatedAdTests {

        @Test
        public void whenUserExists_retrieveAds(){
            // Retrieve the user and category
            User user = userRepository.findAll().get(0);
            Category category = categoryRepository.findAll().get(0);

            // Building an ad without persisting it
            Ad newAd = Ad.builder().
                    title("title").
                    description("").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.HOUR).
                    duration(2).
                    price(100).
                    streetAddress("address").
                    postalCode(7999).
                    user(user).
                    category(category).
                    build();

            // Save the ad with the foreign keys
            newAd = adRepository.save(newAd);

            // Due to the foreign keys, the user now also has this ad
            Response response = adService.getAllAdsByUser(user.getId());

            // Assert that the response and the ad is equal
            assertEquals(newAd, response.getObject());
            assertEquals(newAd, response.getBody());
            assertEquals(HttpStatus.OK, response.getStatus());
        }

        @Test
        public void whenUserDoesNotExist_adsAreNotReturned(){
            User user = userRepository.findAll().get(0);

            // Random id
            Long wrongUserId = 101L;

            try {
                // The method will fail when wrong id is used
                adService.getAllAvailableAdsByUser(wrongUserId);
                fail();

            }catch(NoSuchElementException e){
                // Method works correctly if this exception is caught
            }
        }

        @Test
        public void paginationWorks(){

            // Build new user and category
            User user = User.builder().
                    firstName("firstName").
                    lastName("lastName").
                    email("user.name@hotmail.com").
                    password("pass1word").
                    build();


            // Persist the user --> we now have two users
            userRepository.save(user);

            // Fetch the two users
            User user1 = userRepository.findAll().get(0);

            // Fetch the two categories
            Category category1 = categoryRepository.findAll().get(0);

            // Create and save 15 new posts
            for(int i = 0; i < 15; i++){

                // Building an ad
                Ad newAd = Ad.builder().
                        title("title").
                        description("").
                        rental(true).
                        rentedOut(false).
                        durationType(AdType.HOUR).
                        duration(2).
                        price(100).
                        streetAddress("address").
                        postalCode(7999).
                        user(user1).
                        category(category1).
                        build();

                // Save the new ad
                adRepository.save(newAd);
            }
            // Pagination with all 15 ads
            Response response = adService.getPageOfAds(15);
            assertEquals(response.getStatus(), HttpStatus.OK);
        }

        @Test
        public void paginationDoesNotWork_WhenNotEnoughAds(){

            // Build new user and category
            User user = User.builder().
                    firstName("firstName").
                    lastName("lastName").
                    email("user.name@hotmail.com").
                    password("pass1word").
                    build();


            // Persist the user --> we now have two users
            userRepository.save(user);

            // Fetch the two users
            User user1 = userRepository.findAll().get(0);

            // Fetch the two categories
            Category category1 = categoryRepository.findAll().get(0);

            // Create and save 15 new posts
            for(int i = 0; i < 15; i++){

                // Building an ad
                Ad newAd = Ad.builder().
                        title("title").
                        description("").
                        rental(true).
                        rentedOut(false).
                        durationType(AdType.HOUR).
                        duration(2).
                        price(100).
                        streetAddress("address").
                        postalCode(7999).
                        user(user1).
                        category(category1).
                        build();

                // Save the new ad
                adRepository.save(newAd);
            }
            // Pagination with all 16 ads (only 15 present in db)
            Response response = adService.getPageOfAds(16);
            assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST); //TODO this is not the actual
        }
    }


    @Nested
    class AdGetRelatedTests{
        @Test
        public void correct_pagination(){
            //Create new objects
            User user = new User("johan@normann.no", "abc", "Johan", "Normann", "Trondheim",
                    Date.valueOf("2001-01-01"), null, null);
            userRepository.save(user);
            User user1 = userRepository.findAll().get(0);
            User user2 = userRepository.findAll().get(1);
            Category category1 = categoryRepository.findAll().get(0);
            Category category2 = categoryRepository.findAll().get(1);

            //Save 10 new posts
            for(int i=0; i<10; i++){
                Post post = new Post("Hammer " + (i+1), 40, category1, "", "Trondheim", user1, new HashSet<>());
                postRepository.save(post);
            }

            //Ensure correct result size and correct objects found
            Page<Post> posts = postService.getPosts(0, 10);
            assertEquals(10, posts.toList().size());
            for(Post aPost : posts){
                assertTrue(aPost.getTitle().contains("Hammer"));
            }

            //Ensure correct page size when splitting
            Page<Post> posts1 = postService.getPosts(0, 5);
            Page<Post> posts2 = postService.getPosts(1, 5);
            assertEquals(5, posts1.toList().size());
            assertEquals(5, posts2.toList().size());

            //Ensure no duplicates in the two pages
            for(Post post1 : posts1){
                for(Post post2 : posts2){
                    assertNotEquals(post1.getPostId(), post2.getPostId());
                }
            }
        }
    }

    @Nested
    class searchPosts{
        private void fill_test_data(){
            User user = userRepository.findAll().get(0);
            Category category1 = categoryRepository.findAll().get(0);
            Category category2 = categoryRepository.findAll().get(1);
            Category[] categories = new Category[]{category1, category2};
            String[] titles = new String[]{"Hammer", "Sag", "Høgtalar"};
            String[] locations = new String[]{"Trondheim", "Bergen", "Oslo"};

            for(Category category : categories){
                for(String title : titles){
                    for(String location : locations){
                        Post post = new Post(title, 40, category, "", location, user, new HashSet<>());
                        postRepository.save(post);
                    }
                }
            }
        }

        @Test
        public void handles_empty_search(){
            fill_test_data();

            //Send empty search request
            PostRequest request = new PostRequest("","","");
            List<Post> posts1 = postService.searchPosts(0, 10, request).toList();
            List<Post> posts2 = postService.searchPosts(1, 10, request).toList();

            //Ensure correct response size
            assertEquals(posts1.size(), 10);
            assertEquals(posts2.size(), 8);
        }

        @Test
        public void handles_search_by_one_arg(){
            fill_test_data();

            //Search by title
            PostRequest request = new PostRequest("Hammer","","");
            Page<Post> posts = postService.searchPosts(0, 10, request);
            List<Post> postList = posts.toList();

            assertEquals(postList.size(), 6);

            //Ensure only title got filtered
            for(Post post : postList){
                assertEquals(post.getTitle(), "Hammer");
            }
        }

        @Test
        public void handles_search_by_all_args(){
            fill_test_data();

            //Search by all args
            PostRequest request = new PostRequest("Hammer", "Trondheim", "Tools");
            Page<Post> posts = postService.searchPosts(0, 10, request);

            //Ensure correct response size
            assertEquals(posts.toList().size(), 1);

            //Ensure correct response
            Post post = posts.toList().get(0);
            assertEquals(post.getTitle(), "Hammer");
            assertEquals(post.getLocation(), "Trondheim");
            assertEquals(post.getCategory().getName(), "Tools");
        }

        @Test
        public void handles_invalid_args(){
            fill_test_data();

            //Search by incorrect title
            PostRequest request1 = new PostRequest("Trillebår","","");
            Page<Post> posts1 = postService.searchPosts(0, 10, request1);

            //Search by incorrect location
            PostRequest request2 = new PostRequest("","Molde","");
            Page<Post> posts2 = postService.searchPosts(0, 10, request2);

            //Search by incorrect category
            PostRequest request3 = new PostRequest("","","Toys");
            Page<Post> posts3 = postService.searchPosts(0, 10, request3);

            //Ensure all results are empty
            assertEquals(posts1.toList().size(), 0);
            assertEquals(posts2.toList().size(), 0);
            assertEquals(posts3.toList().size(), 0);
        }
    }
}
