package com.example.idatt2106_2022_05_backend.config;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.CalendarDate;
import com.example.idatt2106_2022_05_backend.model.Category;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CalendarDateRepository;
import com.example.idatt2106_2022_05_backend.repository.CategoryRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Class loads in data for use in db upon start of application
 */
@Component
public class DataLoader implements ApplicationRunner {


        private UserRepository userRepository;

        private AdRepository adRepository;

        private CategoryRepository categoryRepository;

        private CalendarDateRepository calDateRepository;

        private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    /**
     * Constructor of the class.
     *
     * @param userRepository repository of the {@link User} object
     * @param adRepository repository of the {@link Ad} object
     * @param categoryRepository repository of the {@link Category} object
     * @param calDateRepository repository of the {@link CalendarDate} object
     */
    public DataLoader(UserRepository userRepository, AdRepository adRepository,
                          CategoryRepository categoryRepository, CalendarDateRepository calDateRepository) {

            this.userRepository = userRepository;
            this.adRepository = adRepository;
            this.categoryRepository = categoryRepository;
            this.calDateRepository = calDateRepository;
        }

        public void run(ApplicationArguments args) {

            // Create users
            User user1 = User.builder()
                    .firstName("Anders")
                    .lastName("Tellefsen")
                    .email("andetel@stud.ntnu.no")
                    .password(passwordEncoder.encode("passord123"))
                    .build();
            User user2 = User.builder()
                    .firstName("Brage")
                    .lastName("Minge")
                    .email("bragem@stud.ntnu.no")
                    .password(passwordEncoder.encode("passord123"))
                    .build();

            User user3 = User.builder()
                    .firstName("Hasan")
                    .lastName("Rehman")
                    .email("hasano@stud.ntnu.no")
                    .password(passwordEncoder.encode("passord123"))
                    .build();

            User user4 = User.builder()
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
            Category category1 = Category.builder().name("Food").parent(true).build();
            Category category2 = Category.builder().name("Clothes").parent(true).build();
            Category category3 = Category.builder().name("Equipment").parent(true).build();

            // Persist main-categories
            categoryRepository.save(category1);
            categoryRepository.save(category2);
            categoryRepository.save(category3);

            System.out.println("categories: " + categoryRepository.findAll());

            // Create sub-categories
            Category category4 = Category.builder().name("Fruits").
                    parentName(category1.getName()).build();
            Category category5 = Category.builder().name("Pants").
                    parentName(category2.getName()).build();
            Category category6 = Category.builder().name("IT").
                    parentName(category3.getName()).build();

            // Persist sub-categories
            categoryRepository.save(category4);
            categoryRepository.save(category5);
            categoryRepository.save(category6);


            // Create ad
            Ad pants = Ad.builder().
                    title("New pants").
                    description("Renting out a pair of pants in size 36").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(100).
                    streetAddress("Project Road 4").
                    postalCode(7200).
                    user(user1).
                    category(category5).
                    build();

            Ad fruit = Ad.builder().
                    title("You may borrow fruit").
                    description("Renting out 12 grapes").
                    rental(true).
                    durationType(AdType.WEEK).
                    duration(2).
                    price(150).
                    streetAddress("Project Road 5").
                    postalCode(7000).
                    user(user2).
                    category(category4).
                    build();

            Ad pc = Ad.builder().
                    title("Pc rental").
                    description("Renting out a new lenovo").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(800).
                    streetAddress("Project Road 6").
                    postalCode(7800).
                    user(user3).
                    category(category6).
                    build();

            // Persist the 3 ads
            adRepository.save(pants);
            adRepository.save(fruit);
            adRepository.save(pc);

            // Adding the sets
            Set<Ad> ads1 = new HashSet<>();
            ads1.add(fruit);
            category4.setAds(ads1);

            Set<Ad> ads2 = new HashSet<>();
            ads2.add(pants);
            category5.setAds(ads2);

            Set<Ad> ads3 = new HashSet<>();
            ads3.add(pc);
            category6.setAds(ads3);

            categoryRepository.save(category4);
            categoryRepository.save(category5);
            categoryRepository.save(category6);
        }
}
