package com.example.idatt2106_2022_05_backend.controller;


import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
//@ActiveProfiles("test")
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
//public class PostIntegrationTest {

//    @Autowired
//    AdService postService;
//    @Autowired
//    AdRepository postRepository;
//
//    @Autowired
//    UserRepository userRepository;
//    @Autowired
//    OrganizationRepository orgRepository;
//    @Autowired
//    UserOrganizationRepository userOrgRepository;
//    @Autowired
//    CategoryRepository categoryRepository;
//
//    @BeforeEach
//    public void fillTestData() {
//        User user = new User("ola@normann.no", "abc", "Ola", "Normann", "Trondheim",
//                Date.valueOf("2001-01-01"), null, null);
//        userRepository.save(user);
//
//        Organization org = new Organization(40L, "Coop Bygg");
//        orgRepository.save(org);
//
//        Category tools = new Category("Tools");
//        categoryRepository.save(tools);
//
//        Category electronics = new Category("Electronics");
//        categoryRepository.save(electronics);
//    }
//
//    @AfterEach
//    public void emptyDatabase() {
//        postRepository.deleteAll();
//        userRepository.deleteAll();
//        orgRepository.deleteAll();
//        userOrgRepository.deleteAll();
//        categoryRepository.deleteAll();
//    }
//
//    @Nested
//    class add{
//        @Test
//        public void handles_correct_foreign_keys(){
//            //Verify that table is empty
//            assertEquals(0, postRepository.findAll().size());
//
//            //Save new post
//            User user = userRepository.findAll().get(0);
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", user, new HashSet<>());
//            try {
//                postService.add(post, user);
//            }catch(IllegalAccessException | NoSuchElementException e){
//                fail();
//            }
//
//            //Verify that the post is saved
//            assertTrue(postRepository.findAll().size() > 0);
//            assertEquals(postRepository.findAll().get(0).getTitle(), "Hammer");
//        }
//
//        @Test
//        public void handles_wrong_foreign_keys(){
//            //Create new objects but don't save them
//            User user = new User("johan@normann.no", "abc", "Johan", "Normann", "Trondheim",
//                    Date.valueOf("2001-01-01"), null, null);
//            Category category = new Category("Kategori");
//
//            //Try to save post
//            Post post = new Post("Hammer", 40, category, "", "Trondheim", user, new HashSet<>());
//            try {
//                postService.add(post, user);
//                fail();
//            }catch(NoSuchElementException e){
//                //pass test
//            }catch(IllegalAccessException e){
//                fail();
//            }
//        }
//
//        @Test
//        public void user_in_org_can_add_post_to_org(){
//            //Add user to organization
//            User user = userRepository.findAll().get(0);
//            Organization organization = orgRepository.findAll().get(0);
//            UserOrganization userOrg = new UserOrganization(user, organization, OrganizationRole.EMPLOYEE);
//            userOrgRepository.save(userOrg);
//
//            //Add post in organization from user
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", user, organization, new HashSet<>());
//            try{
//                postService.add(post, user);
//            }catch(IllegalAccessException | NoSuchElementException e){
//                fail();
//            }
//        }
//
//        @Test
//        public void user_out_of_org_cant_add_post_to_org(){
//            User user = userRepository.findAll().get(0);
//            Organization organization = orgRepository.findAll().get(0);
//
//            //Try to add post in organization from user
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", user, organization, new HashSet<>());
//            try{
//                postService.add(post, user);
//                fail();
//            }catch(IllegalAccessException e){
//                //pass test
//            }catch(NoSuchElementException e){
//                fail();
//            }
//        }
//    }
//
//    @Nested
//    class edit{
//        @Test
//        public void method_edits_post(){
//            //Save a post
//            User user = userRepository.findAll().get(0);
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", user, new HashSet<>());
//            post = postRepository.save(post);
//            assertTrue(postRepository.existsById(post.getPostId()));
//
//            //Edit post
//            post.setTitle("Sag");
//            try {
//                postService.edit(post, user);
//                post = postRepository.findById(post.getPostId()).get();
//                assertEquals("Sag", post.getTitle());
//            }catch(NoSuchElementException | IllegalAccessException e){
//                fail();
//            }
//        }
//
//        @Test
//        public void handles_non_existing_post(){
//            User user = userRepository.findAll().get(0);
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", user, new HashSet<>());
//            try{
//                postService.edit(post, user);
//                fail();
//            }catch(NoSuchElementException e){
//                //pass test
//            }catch(IllegalAccessException e){
//                fail();
//            }
//        }
//
//        @Test
//        public void handles_different_user(){
//            //Save post from user and save otherUser
//            User user = userRepository.findAll().get(0);
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", user, new HashSet<>());
//            postRepository.save(post);
//            User otherUser = new User("wrong@user.com", "abc", "Johan", "Normann", "Trondheim",
//                    Date.valueOf("2001-01-01"), null, null);
//            userRepository.save(otherUser);
//
//            //Try to edit post from otherUser
//            try{
//                post.setTitle("Sag");
//                postService.edit(post, otherUser);
//                fail();
//            }catch(NoSuchElementException e){
//                fail();
//            }catch(IllegalAccessException e){
//                //pass test
//            }
//        }
//
//        @Test
//        public void allows_admin_of_org_to_edit(){
//            //Add user to organization
//            User user = userRepository.findAll().get(0);
//            Organization organization = orgRepository.findAll().get(0);
//            UserOrganization userOrg = new UserOrganization(user, organization, OrganizationRole.EMPLOYEE);
//            userOrgRepository.save(userOrg);
//
//            //Add post in organization from user
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", user, organization, new HashSet<>());
//            postRepository.save(post);
//
//            //Edit post
//            try{
//                post.setTitle("Sag");
//                postService.edit(post, user);
//            }catch(IllegalAccessException | NoSuchElementException e){
//                fail();
//            }
//        }
//
//        @Test
//        public void handles_non_existing_category(){
//            //Save new post
//            User user = userRepository.findAll().get(0);
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", user, new HashSet<>());
//            postRepository.save(post);
//
//            //Create wrong category
//            Category category = new Category("Wrong");
//
//            //Try to edit post
//            try{
//                post.setCategory(category);
//                postService.edit(post, user);
//                fail();
//            }catch(IllegalAccessException e){
//                fail();
//            }catch(NoSuchElementException e){
//                //pass test
//            }
//        }
//    }
//
//    @Nested
//    class delete{
//        @Test
//        public void handles_existing_post(){
//            assertEquals(0, postRepository.findAll().size());
//            User user = userRepository.findAll().get(0);
//
//            //Save new post
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", userRepository.findAll().get(0), new HashSet<>());
//            Long postId = postRepository.save(post).getPostId();
//
//            assertEquals(1, postRepository.findAll().size());
//
//            //Delete the new post
//            try{
//                postService.delete(postId, user);
//            }catch(IllegalAccessException e){
//                fail();
//            }
//
//            assertEquals(0, postRepository.findAll().size());
//        }
//
//        @Test
//        public void handles_wrong_post(){
//            assertEquals(0, postRepository.findAll().size());
//            User user = userRepository.findAll().get(0);
//
//            try {
//                postService.delete(1L, user);
//                fail();
//            }catch(IllegalAccessException e){
//                fail();
//            }catch(NoSuchElementException e){
//                //pass test
//            }
//        }
//
//        @Test
//        public void handles_wrong_user(){
//            //Create new user
//            User wrongUser = new User("johan@normann.no", "abc", "Johan", "Normann", "Trondheim",
//                    Date.valueOf("2001-01-01"), null, null);
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", userRepository.findAll().get(0), new HashSet<>());
//            Long postId = postRepository.save(post).getPostId();
//
//            try {
//                postService.delete(postId, wrongUser);
//                fail();
//            }catch(IllegalAccessException e){
//                //pass test
//            }
//        }
//
//        @Test
//        public void admin_can_delete_org_post(){
//            //Create objects
//            Organization org = orgRepository.findAll().get(0);
//            User employee = userRepository.findAll().get(0);
//
//            //Create new post from employee in the organization
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", employee, org, new HashSet<>());
//            postRepository.save(post);
//
//            //Make an admin of the organization
//            User admin = new User("johan@normann.no", "abc", "Johan", "Normann", "Trondheim",
//                    Date.valueOf("2001-01-01"), null, null);
//            admin = userRepository.save(admin);
//            UserOrganization userOrg = new UserOrganization(admin, org, OrganizationRole.ADMIN);
//            userOrgRepository.save(userOrg);
//
//            //Make the admin try to delete the employees post
//            try {
//                postService.delete(post.getPostId(), admin);
//            }catch(IllegalAccessException e){
//                fail();
//            }
//        }
//    }
//
//    @Test
//    public void handles_invalid_page_args(){
//        //This method is called by multiple methods, testing one will test all cases.
//        try{
//            Page<Post> posts = postService.getByUser(-5, 5, 1L);
//            fail();
//        }catch(IllegalArgumentException e){
//            //pass test
//        }
//    }
//
//    @Nested
//    class getByUser{
//        @Test
//        public void handles_existing_user(){
//            //Save post
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", userRepository.findAll().get(0), new HashSet<>());
//            postRepository.save(post);
//
//            //Get posts for user
//            Long userId = userRepository.findAll().get(0).getId();
//            Page<Post> posts = postService.getByUser(0, 5, userId);
//
//            assertEquals(1, posts.getTotalElements());
//            assertEquals("Hammer", posts.get().toList().get(0).getTitle());
//        }
//
//        @Test
//        public void handles_invalid_user(){
//            Long correctUserId = userRepository.findAll().get(0).getId();
//            Long wrongUserId = correctUserId + 1;
//
//            try {
//                Page<Post> posts = postService.getByUser(0, 5, wrongUserId);
//                fail();
//            }catch(NoSuchElementException e){
//                //pass test
//            }
//        }
//
//        @Test
//        public void correct_pagination(){
//            //Create new objects
//            User user = new User("johan@normann.no", "abc", "Johan", "Normann", "Trondheim",
//                    Date.valueOf("2001-01-01"), null, null);
//            userRepository.save(user);
//            User user1 = userRepository.findAll().get(0);
//            User user2 = userRepository.findAll().get(1);
//            Category category1 = categoryRepository.findAll().get(0);
//            Category category2 = categoryRepository.findAll().get(1);
//
//            //Save 10 new posts
//            for(int i=0; i<6; i++){
//                Post post = new Post("Hammer " + (i+1), 40, category1, "", "Trondheim", user1, new HashSet<>());
//                postRepository.save(post);
//            }
//            for(int i=0; i<6; i++){
//                Post post = new Post("Sag " + (i+1), 50, category2, "", "Oslo", user2, new HashSet<>());
//                postRepository.save(post);
//            }
//
//            //Ensure correct result size and correct objects found for user 1
//            Page<Post> posts1 = postService.getByUser(0, 6, user1.getId());
//            assertEquals(6, posts1.toList().size());
//            for(Post aPost : posts1){
//                assertTrue(aPost.getTitle().contains("Hammer"));
//                assertFalse(aPost.getTitle().contains("Sag"));
//            }
//
//            //Ensure correct result size and correct objects found for user 2
//            Page<Post> posts2 = postService.getByUser(0, 6, user2.getId());
//            assertEquals(6, posts2.toList().size());
//            for(Post aPost : posts2){
//                assertTrue(aPost.getTitle().contains("Sag"));
//                assertFalse(aPost.getTitle().contains("Hammer"));
//            }
//
//            //Ensure correct page size when splitting
//            posts1 = postService.getByUser(0, 3, user1.getId());
//            posts2 = postService.getByUser(1, 3, user1.getId());
//            assertEquals(3, posts1.toList().size());
//            assertEquals(3, posts2.toList().size());
//
//            //Ensure no duplicates in the two pages
//            for(Post post1 : posts1){
//                for(Post post2 : posts2){
//                    assertNotEquals(post1.getPostId(), post2.getPostId());
//                }
//            }
//        }
//    }
//
//    @Nested
//    class getByOrganization{
//        @Test
//        public void handles_existing_organization(){
//            //Save post
//            Post post = new Post("Hammer", 40, categoryRepository.findAll().get(0), "", "Trondheim", userRepository.findAll().get(0), orgRepository.findAll().get(0), new HashSet<>());
//            postRepository.save(post);
//
//            //Get posts for organization
//            Long orgNum = orgRepository.findAll().get(0).getOrgNum();
//            Page<Post> posts = postService.getByOrganization(0, 5, orgNum);
//
//            assertEquals(1, posts.getTotalElements());
//            assertEquals("Hammer", posts.get().toList().get(0).getTitle());
//        }
//
//        @Test
//        public void handles_invalid_organization(){
//            Long correctOrgNum = orgRepository.findAll().get(0).getOrgNum();
//            Long wrongOrgNum = correctOrgNum + 1;
//
//            try {
//                Page<Post> posts = postService.getByOrganization(0, 5, wrongOrgNum);
//                fail();
//            }catch(NoSuchElementException e){
//                //pass test
//            }
//        }
//
//        @Test
//        public void correct_pagination(){
//            //Create new objects
//            Organization org = new Organization(50L, "IKEA Leangen");
//            orgRepository.save(org);
//            Organization org1 = orgRepository.findAll().get(0);
//            Organization org2 = orgRepository.findAll().get(1);
//            User user = userRepository.findAll().get(0);
//            Category category1 = categoryRepository.findAll().get(0);
//            Category category2 = categoryRepository.findAll().get(1);
//
//            //Save 10 new posts
//            for(int i=0; i<6; i++){
//                Post post = new Post("Hammer " + (i+1), 40, category1, "", "Trondheim", user, org1, new HashSet<>());
//                postRepository.save(post);
//            }
//            for(int i=0; i<6; i++){
//                Post post = new Post("Sag " + (i+1), 50, category2, "", "Oslo", user, org2, new HashSet<>());
//                postRepository.save(post);
//            }
//
//            //Ensure correct result size and correct objects found for organization 1
//            Page<Post> posts1 = postService.getByOrganization(0, 6, org1.getOrgNum());
//            assertEquals(6, posts1.toList().size());
//            for(Post aPost : posts1){
//                assertTrue(aPost.getTitle().contains("Hammer"));
//                assertFalse(aPost.getTitle().contains("Sag"));
//            }
//
//            //Ensure correct result size and correct objects found for organization 2
//            Page<Post> posts2 = postService.getByOrganization(0, 6, org2.getOrgNum());
//            assertEquals(6, posts2.toList().size());
//            for(Post aPost : posts2){
//                assertTrue(aPost.getTitle().contains("Sag"));
//                assertFalse(aPost.getTitle().contains("Hammer"));
//            }
//
//            //Ensure correct page size when splitting
//            posts1 = postService.getByOrganization(0, 3, org1.getOrgNum());
//            posts2 = postService.getByOrganization(1, 3, org2.getOrgNum());
//            assertEquals(3, posts1.toList().size());
//            assertEquals(3, posts2.toList().size());
//
//            //Ensure no duplicates in the two pages
//            for(Post post1 : posts1){
//                for(Post post2 : posts2){
//                    assertNotEquals(post1.getPostId(), post2.getPostId());
//                }
//            }
//        }
//    }
//
//    @Nested
//    class getPosts{
//        @Test
//        public void correct_pagination(){
//            //Create new objects
//            User user = new User("johan@normann.no", "abc", "Johan", "Normann", "Trondheim",
//                    Date.valueOf("2001-01-01"), null, null);
//            userRepository.save(user);
//            User user1 = userRepository.findAll().get(0);
//            User user2 = userRepository.findAll().get(1);
//            Category category1 = categoryRepository.findAll().get(0);
//            Category category2 = categoryRepository.findAll().get(1);
//
//            //Save 10 new posts
//            for(int i=0; i<10; i++){
//                Post post = new Post("Hammer " + (i+1), 40, category1, "", "Trondheim", user1, new HashSet<>());
//                postRepository.save(post);
//            }
//
//            //Ensure correct result size and correct objects found
//            Page<Post> posts = postService.getPosts(0, 10);
//            assertEquals(10, posts.toList().size());
//            for(Post aPost : posts){
//                assertTrue(aPost.getTitle().contains("Hammer"));
//            }
//
//            //Ensure correct page size when splitting
//            Page<Post> posts1 = postService.getPosts(0, 5);
//            Page<Post> posts2 = postService.getPosts(1, 5);
//            assertEquals(5, posts1.toList().size());
//            assertEquals(5, posts2.toList().size());
//
//            //Ensure no duplicates in the two pages
//            for(Post post1 : posts1){
//                for(Post post2 : posts2){
//                    assertNotEquals(post1.getPostId(), post2.getPostId());
//                }
//            }
//        }
//    }
//
//    @Nested
//    class searchPosts{
//        private void fill_test_data(){
//            User user = userRepository.findAll().get(0);
//            Category category1 = categoryRepository.findAll().get(0);
//            Category category2 = categoryRepository.findAll().get(1);
//            Category[] categories = new Category[]{category1, category2};
//            String[] titles = new String[]{"Hammer", "Sag", "Høgtalar"};
//            String[] locations = new String[]{"Trondheim", "Bergen", "Oslo"};
//
//            for(Category category : categories){
//                for(String title : titles){
//                    for(String location : locations){
//                        Post post = new Post(title, 40, category, "", location, user, new HashSet<>());
//                        postRepository.save(post);
//                    }
//                }
//            }
//        }
//
//        @Test
//        public void handles_empty_search(){
//            fill_test_data();
//
//            //Send empty search request
//            PostRequest request = new PostRequest("","","");
//            List<Post> posts1 = postService.searchPosts(0, 10, request).toList();
//            List<Post> posts2 = postService.searchPosts(1, 10, request).toList();
//
//            //Ensure correct response size
//            assertEquals(posts1.size(), 10);
//            assertEquals(posts2.size(), 8);
//        }
//
//        @Test
//        public void handles_search_by_one_arg(){
//            fill_test_data();
//
//            //Search by title
//            PostRequest request = new PostRequest("Hammer","","");
//            Page<Post> posts = postService.searchPosts(0, 10, request);
//            List<Post> postList = posts.toList();
//
//            assertEquals(postList.size(), 6);
//
//            //Ensure only title got filtered
//            for(Post post : postList){
//                assertEquals(post.getTitle(), "Hammer");
//            }
//        }
//
//        @Test
//        public void handles_search_by_all_args(){
//            fill_test_data();
//
//            //Search by all args
//            PostRequest request = new PostRequest("Hammer", "Trondheim", "Tools");
//            Page<Post> posts = postService.searchPosts(0, 10, request);
//
//            //Ensure correct response size
//            assertEquals(posts.toList().size(), 1);
//
//            //Ensure correct response
//            Post post = posts.toList().get(0);
//            assertEquals(post.getTitle(), "Hammer");
//            assertEquals(post.getLocation(), "Trondheim");
//            assertEquals(post.getCategory().getName(), "Tools");
//        }
//
//        @Test
//        public void handles_invalid_args(){
//            fill_test_data();
//
//            //Search by incorrect title
//            PostRequest request1 = new PostRequest("Trillebår","","");
//            Page<Post> posts1 = postService.searchPosts(0, 10, request1);
//
//            //Search by incorrect location
//            PostRequest request2 = new PostRequest("","Molde","");
//            Page<Post> posts2 = postService.searchPosts(0, 10, request2);
//
//            //Search by incorrect category
//            PostRequest request3 = new PostRequest("","","Toys");
//            Page<Post> posts3 = postService.searchPosts(0, 10, request3);
//
//            //Ensure all results are empty
//            assertEquals(posts1.toList().size(), 0);
//            assertEquals(posts2.toList().size(), 0);
//            assertEquals(posts3.toList().size(), 0);
//        }
//    }
//}
