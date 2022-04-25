package com.example.idatt2106_2022_05_backend.config;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CategoryRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Class loads in data for use in db upon start of application
 */
@Component
public class DataLoader implements ApplicationRunner {


        private UserRepository userRepository;

        private AdRepository adRepository;

        private CategoryRepository categoryRepository;

        private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


        /**
         * Constructor of the class.
         *
         * @param userRepository repository of the {@link User} object
         * @param adRepository repository of the {@link Ad} object
         * @param categoryRepository repository of the {@link Category} object
         */
        public DataLoader(UserRepository userRepository, AdRepository adRepository,
                          CategoryRepository categoryRepository) {

            this.userRepository = userRepository;
            this.adRepository = adRepository;
            this.categoryRepository = categoryRepository;
        }

        public void run(ApplicationArguments args) {

            // Create users
            User user1 = User.builder()
                    .id(1L)
                    .firstName("Anders")
                    .lastName("Tellefsen")
                    .email("andetel@stud.ntnu.no")
                    .password(passwordEncoder.encode("passord123"))
                    .build();
            User user2 = User.builder()
                    .id(2L)
                    .firstName("Brage")
                    .lastName("Minge")
                    .email("bragem@stud.ntnu.no")
                    .password(passwordEncoder.encode("passord123"))
                    .build();

            User user3 = User.builder()
                    .id(3L)
                    .firstName("Hasan")
                    .lastName("Rehman")
                    .email("hasano@stud.ntnu.no")
                    .password(passwordEncoder.encode("passord123"))
                    .build();

            User user4 = User.builder()
                    .id(4L)
                    .firstName("Daniel")
                    .lastName("Danielsen")
                    .email("daniel@gmail.com")
                    .password(passwordEncoder.encode("passord123"))
                    .build();

            // Persist the users

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(user4);


            // Create main-categories
            Category category1 = Category.builder().id(5L).name("Food").build();
            Category category2 = Category.builder().id(6L).name("Clothes").build();
            Category category3 = Category.builder().id(7L).name("Equipment").build();

            // Persist main-categories
            //categoryRepository.save(category1);
            //categoryRepository.save(category2);
            //categoryRepository.save(category3);

            // Create sub-categories
            Category category4 = Category.builder().id(8L).name("Fruits").
                    mainCategory(category1).build();
            Category category5 = Category.builder().id(9L).name("Pants").
                    mainCategory(category2).build();
            Category category6 = Category.builder().id(10L).name("IT").
                    mainCategory(category3).build();

            // Persist sub-categories
            categoryRepository.save(category4);
            categoryRepository.save(category5);
            categoryRepository.save(category6);

            // Add sub-categories to sets
            Set<Category> subCategories1 = new HashSet<>();
            subCategories1.add(category4);
            Set<Category> subCategories2 = new HashSet<>();
            subCategories2.add(category5);
            Set<Category> subCategories3 = new HashSet<>();
            subCategories3.add(category6);

            // Add sub-categories as FK to the main categories
            category1.setSubCategories(subCategories1);
            category2.setSubCategories(subCategories2);
            category3.setSubCategories(subCategories3);

            // Persist (update) the categories
            //categoryRepository.save(category1);
            //categoryRepository.save(category2);
            //categoryRepository.save(category3);


            // Create ad
            Ad pants = Ad.builder().
                    id(11L).
                    title("New pants").
                    description("Renting out a pair of pants in size 36").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(100).
                    streetAddress("Project Road 4").
                    postalCode(7200).
                    user(user1).
                    category(category5).
                    build();

            Ad fruit = Ad.builder().id(12L).
                    title("You may borrow fruit").
                    description("Renting out 12 grapes").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.WEEK).
                    duration(2).
                    price(150).
                    streetAddress("Project Road 5").
                    postalCode(7000).
                    user(user2).
                    category(category4).
                    build();

            Ad pc = Ad.builder().id(13L).
                    title("Pc rental").
                    description("Renting out a new lenovo").
                    rental(true).
                    rentedOut(false).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(800).
                    streetAddress("Project Road 6").
                    postalCode(7800).
                    user(user3).
                    category(category6).
                    build();

            // Persist the 3 ads
            //adRepository.save(pants);
            //adRepository.save(fruit);
            //adRepository.save(pc);
        }
}
