package com.example.idatt2106_2022_05_backend.config;

import com.example.idatt2106_2022_05_backend.dto.ad.AdDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.service.calendar.CalendarService;
import com.example.idatt2106_2022_05_backend.util.PictureUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class loads in data for use in db upon start of application
 */
@Component
public class DataLoader implements ApplicationRunner {

        private UserRepository userRepository;

        private AdRepository adRepository;

        private CategoryRepository categoryRepository;

        private CalendarDateRepository calDateRepository;

        private RentalRepository rentalRepository;

        private ReviewRepository reviewRepository;

        private PictureRepository pictureRepository;

        private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        @Autowired
        CalendarService calendarService;

        @Autowired
        AdService adService;

    /**
     * Constructor of the class.
     *
     * @param userRepository repository of the {@link User} object
     * @param adRepository repository of the {@link Ad} object
     * @param categoryRepository repository of the {@link Category} object
     * @param calDateRepository repository of the {@link CalendarDate} object
     */
    public DataLoader(UserRepository userRepository, AdRepository adRepository,
                          CategoryRepository categoryRepository, CalendarDateRepository calDateRepository,
                      RentalRepository rentalRepository, ReviewRepository reviewRepository,
                      PictureRepository pictureRepository) {

            this.userRepository = userRepository;
            this.adRepository = adRepository;
            this.categoryRepository = categoryRepository;
            this.calDateRepository = calDateRepository;
            this.rentalRepository = rentalRepository;
            this.reviewRepository = reviewRepository;
            this.pictureRepository = pictureRepository;
        }

        public void run(ApplicationArguments args) throws IOException {

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
            Category category1 = Category.builder().name("Food").parent(true).icon("fa-carrot").build();
            Category category2 = Category.builder().name("Clothes").parent(true).icon("fa-socks").build();
            Category category3 = Category.builder().name("Equipment").parent(true).icon("fa-desktop").build();

            // Persist main-categories
            categoryRepository.save(category1);
            categoryRepository.save(category2);
            categoryRepository.save(category3);

            System.out.println("categories: " + categoryRepository.findAll());

            // Create sub-categories
            Category category4 = Category.builder().name("Fruits").icon("").
                    parentName(category1.getName()).build();
            Category category5 = Category.builder().name("Pants").icon("").
                    parentName(category2.getName()).build();
            Category category6 = Category.builder().name("IT").icon("").
                    parentName(category3.getName()).build();

            // Add a new category as well for testing!
            Category category7 = Category.builder().name("Shoes").
                    parentName(category1.getName()).build();

            // Persist sub-categories
            categoryRepository.save(category4);
            categoryRepository.save(category5);
            categoryRepository.save(category6);
            categoryRepository.save(category7);

            // Create sub category of sub-category
            Category category8 = Category.builder().name("Chargers").
                    parentName(category6.getName()).build();

            categoryRepository.save(category8);


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
                    city("Trondheim").
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
                    city("Trondheim").
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
                    city("Trondheim").
                    user(user3).
                    category(category6).
                    build();

            Ad charger = Ad.builder().
                    title("Pc charger").
                    description("Renting out a new lenovo charger").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(800).
                    streetAddress("Project Road 6").
                    postalCode(7800).
                    city("Trondheim").
                    user(user3).
                    category(category8).
                    build();
            Ad motherBoard = Ad.builder().
                    title("Mother board").
                    description("Renting out a new lenovo motherboard").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(800).
                    streetAddress("Project Road 6").
                    postalCode(7800).
                    city("Trondheim").
                    user(user3).
                    category(category3).
                    build();

            Ad p = Ad.builder().
                    title("Pets").
                    description("Renting out my pet").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(100).
                    streetAddress("Road 4").
                    postalCode(7202).
                    user(user1).
                    category(category5).
                    build();

            Ad pa = Ad.builder().
                    title("New p").
                    description("pushing p").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(100).
                    streetAddress("Project P").
                    postalCode(7201).
                    user(user1).
                    category(category4).
                    build();

            Ad pan = Ad.builder().
                    title("New news").
                    description("Renting out news").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(100).
                    streetAddress("4 fourty four").
                    postalCode(7200).
                    user(user1).
                    category(category3).
                    build();

            Ad pant = Ad.builder().
                    title("New new").
                    description("Renting out new").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(100).
                    streetAddress("Project Road").
                    postalCode(7200).
                    user(user1).
                    category(category2).
                    build();

            Ad pantss = Ad.builder().
                    title("New pantsss").
                    description("Renting out a pair of pantsss in size 52").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(100).
                    streetAddress("Project 4").
                    postalCode(7200).
                    user(user1).
                    category(category1).
                    build();
            /*
            AdDto skaters = AdDto.builder().
                    title("Patinadoras de secunda mano").
                    city("Pozuelo de Alarcon").rental(true).userId(1).
                    description("patinadoras de tamaño 36").
                    duration(1).durationType(AdType.HOUR).
                    postalCode(28223).price(10).
                    streetAddress("C.Manuel Roses 15C").build();
            */
            // Persist the 3 ads

            adRepository.save(pants);
            adRepository.save(fruit);
            adRepository.save(pc);
            adRepository.save(charger);
            adRepository.save(motherBoard);


            adRepository.save(p);
            adRepository.save(pa);
            adRepository.save(pan);
            adRepository.save(pant);
            adRepository.save(pantss);

            Rental rental = Rental.builder()
                    .ad(pants)
                    .owner(user1)
                    .borrower(user2)
                    .price(10000)
                    .active(false)
                    .deadline(LocalDate.now().plusDays(1))
                    .rentTo(LocalDate.now().plusDays(5))
                    .rentFrom(LocalDate.now().plusDays(2))
                    .dateOfRental(LocalDate.now())
                    .build();

            rentalRepository.save(rental);

            rental = Rental.builder()
                    .ad(pant)
                    .owner(user1)
                    .borrower(user2)
                    .price(1000)
                    .active(false)
                    .deadline(LocalDate.now().plusDays(3))
                    .rentTo(LocalDate.now().plusDays(7))
                    .rentFrom(LocalDate.now().plusDays(4))
                    .dateOfRental(LocalDate.now())
                    .build();
            rentalRepository.save(rental);

            rental = Rental.builder()
                    .ad(pan)
                    .owner(user1)
                    .borrower(user2)
                    .price(100)
                    .active(false)
                    .deadline(LocalDate.now().plusDays(5))
                    .rentTo(LocalDate.now().plusDays(9))
                    .rentFrom(LocalDate.now().plusDays(6))
                    .dateOfRental(LocalDate.now())
                    .build();
            rentalRepository.save(rental);

            rental = Rental.builder()
                    .ad(pa)
                    .owner(user3)
                    .borrower(user1)
                    .price(3000)
                    .active(true)
                    .deadline(LocalDate.now().plusDays(7))
                    .rentTo(LocalDate.now().plusDays(12))
                    .rentFrom(LocalDate.now().plusDays(8))
                    .dateOfRental(LocalDate.now())
                    .build();
            rentalRepository.save(rental);

            rental = Rental.builder()
                    .ad(p)
                    .owner(user3)
                    .borrower(user1)
                    .price(3000)
                    .active(true)
                    .deadline(LocalDate.now().plusDays(9))
                    .rentTo(LocalDate.now().plusDays(15))
                    .rentFrom(LocalDate.now().plusDays(10))
                    .dateOfRental(LocalDate.now())
                    .build();
            rentalRepository.save(rental);

            Review review = Review.builder()
                    .ad(pants)
                    .user(user3)
                    .description("veldig bra anbefaler dette produktet!")
                    .rating(9)
                    .build();
            reviewRepository.save(review);

            review = Review.builder()
                    .ad(pants)
                    .user(user2)
                    .description("Elendig produkt")
                    .rating(6)
                    .build();
            reviewRepository.save(review);

            review = Review.builder()
                    .ad(pants)
                    .user(user3)
                    .description("ten out of ten would buy again")
                    .rating(6)
                    .build();
            reviewRepository.save(review);

            review = Review.builder()
                    .ad(pants)
                    .user(user4)
                    .description("two out of ten would never buy again")
                    .rating(1)
                    .build();
            reviewRepository.save(review);

            // Add dates to the ads // todo might not work due to id
            List<Ad> ads =  adRepository.findAll();
            for(Ad ad : ads) {
                ad.setDates(calendarService.addFutureDates(ad.getId()));
            }

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

            File file = new File("src/main/resources/static/images/random/austin-chan-ukzHlkoz1IE-unsplash.jpg");
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file",
                    file. getName(), "image/jpg", (input).readAllBytes());
            Set<Picture> set = new HashSet<>();
            Picture pic = Picture.builder()
                    .ad(pants)
                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
                    .filename(file.getName())
                    .type("jpg")
                    .build();
            set.add(pic);
            pants.setPictures(new HashSet<>());
            pants.setPictures(set);
            pictureRepository.save(pic);
            adRepository.save(pants);



            file = new File("src/main/resources/static/images/random/bekir-donmez-eofm5R5f9Kw-unsplash.jpg");
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file",
                    file. getName(), "image/jpg", (input).readAllBytes());
            set = new HashSet<>();
            pic = Picture.builder()
                    .ad(pants)
                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
                    .filename(file.getName())
                    .type("jpg")
                    .build();
            set.add(pic);
            pants.setPictures(new HashSet<>());
            pants.setPictures(set);
            pictureRepository.save(pic);
            adRepository.save(pants);

            file = new File("src/main/resources/static/images/random/david-kovalenko-G85VuTpw6jg-unsplash.jpg");
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file",
                    file. getName(), "image/jpg", (input).readAllBytes());
            set = new HashSet<>();
            pic = Picture.builder()
                    .ad(pants)
                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
                    .filename(file.getName())
                    .type("jpg")
                    .build();
            set.add(pic);
            pants.setPictures(new HashSet<>());
            pants.setPictures(set);
            pictureRepository.save(pic);
            adRepository.save(pants);

            file = new File("src/main/resources/static/images/random/diego-ph-fIq0tET6llw-unsplash.jpg");
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file",
                    file. getName(), "image/jpg", (input).readAllBytes());
            set = new HashSet<>();
            pic = Picture.builder()
                    .ad(pants)
                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
                    .filename(file.getName())
                    .type("jpg")
                    .build();
            set.add(pic);
            pants.setPictures(new HashSet<>());
            pants.setPictures(set);
            pictureRepository.save(pic);
            adRepository.save(pants);


            file = new File("src/main/resources/static/images/random/ian-dooley-hpTH5b6mo2s-unsplash.jpg");
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file",
                    file. getName(), "image/jpg", (input).readAllBytes());
            set = new HashSet<>();
            pic = Picture.builder()
                    .ad(fruit)
                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
                    .filename(file.getName())
                    .type("jpg")
                    .build();
            set.add(pic);
            fruit.setPictures(new HashSet<>());
            fruit.setPictures(set);
            pictureRepository.save(pic);
            adRepository.save(pants);


            file = new File("src/main/resources/static/images/random/kristopher-roller-PC_lbSSxCZE-unsplash.jpg");
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file",
                    file. getName(), "image/jpg", (input).readAllBytes());
            set = new HashSet<>();
            pic = Picture.builder()
                    .ad(fruit)
                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
                    .filename(file.getName())
                    .type("jpg")
                    .build();
            set.add(pic);
            fruit.setPictures(new HashSet<>());
            fruit.setPictures(set);
            pictureRepository.save(pic);
            adRepository.save(pants);


            file = new File("src/main/resources/static/images/random/diego-ph-fIq0tET6llw-unsplash.jpg");
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file",
                    file. getName(), "image/jpg", (input).readAllBytes());
            set = new HashSet<>();
            pic = Picture.builder()
                    .ad(fruit)
                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
                    .filename(file.getName())
                    .type("jpg")
                    .build();
            set.add(pic);
            fruit.setPictures(new HashSet<>());
            fruit.setPictures(set);
            pictureRepository.save(pic);
            adRepository.save(pants);

//            Ad ad =
//
//            pants.setDates(calendarService.addFutureDates(savedAd.getId()));
//            pants.setDates(calendarService.addFutureDates(savedAd.getId()));
//            pants.setDates(calendarService.addFutureDates(savedAd.getId()));
//            pants.setDates(calendarService.addFutureDates(savedAd.getId()));
        }
}
