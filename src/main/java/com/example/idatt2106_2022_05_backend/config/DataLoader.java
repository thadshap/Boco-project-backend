package com.example.idatt2106_2022_05_backend.config;

import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.*;
import com.example.idatt2106_2022_05_backend.repository.*;
import com.example.idatt2106_2022_05_backend.service.ad.AdService;
import com.example.idatt2106_2022_05_backend.service.calendar.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class loads in data for use in db upon start of application
 */
@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CalendarDateRepository calDateRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
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
                    .verified(true)
                    .role("User")
                    .build();
            User user2 = User.builder()
                    .firstName("Brage")
                    .lastName("Minge")
                    .email("bragem@stud.ntnu.no")
                    .password(passwordEncoder.encode("passord123"))
                    .verified(true)
                    .role("User")
                    .build();

            User user3 = User.builder()
                    .firstName("Hasan")
                    .lastName("Rehman")
                    .email("hasano@stud.ntnu.no")
                    .password(passwordEncoder.encode("passord123"))
                    .verified(true)
                    .role("User")
                    .build();

            User user4 = User.builder()
                    .firstName("Daniel")
                    .lastName("Danielsen")
                    .email("daniel@gmail.com")
                    .password(passwordEncoder.encode("passord123"))
                    .verified(true)
                    .role("User")
                    .build();

            // Persist the users

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(user4);


            // Create main-categories
            Category tur = Category.builder().name("Tur").parent(true).icon("fa-suitcase").build();
            Category klor = Category.builder().name("Klær").parent(true).icon("fa-socks").build();
            Category redskap = Category.builder().name("Redskap").parent(true).icon("fa-wrench").build();
            Category skole = Category.builder().name("Skole").parent(true).icon("fa-graduation-cap").build();

            // Persist main-categories
            categoryRepository.save(tur);
            categoryRepository.save(klor);
            categoryRepository.save(redskap);
            categoryRepository.save(skole);

            System.out.println("categories: " + categoryRepository.findAll());

            // Create sub-categories
            Category telt = Category.builder().name("Telt").
                    parentName(tur.getName()).build();
            Category build = Category.builder().name("Bårre-maskin").
                    parentName(redskap.getName()).build();
            Category datamaskin = Category.builder().name("Datamaskin").
                    parentName(skole.getName()).build();

            // Add a new category as well for testing!
            Category boker = Category.builder().name("Bøker").
                    parentName(skole.getName()).build();

            Category hammer = Category.builder().name("Hammer").
                    parentName(redskap.getName()).build();

            // Persist sub-categories
            categoryRepository.save(telt);
            categoryRepository.save(build);
            categoryRepository.save(datamaskin);
            categoryRepository.save(hammer);
            categoryRepository.save(boker);

            // Create sub category of sub-category
            Category lader = Category.builder().name("Lader").
                    parentName(datamaskin.getName()).build();

            categoryRepository.save(lader);


            // Create ad
            Ad borre = Ad.builder().
                    title("Borrmaskin").
                    description("Leier ut en kraftig borremaskin, kun 50 kr per time eller 300kr pr dag").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(50).
                    created(LocalDate.now()).
                    lat(63.4).
                    lng(10.4).
                    streetAddress("Gate 4").
                    postalCode(7030).
                    city("Trondheim").
                    user(user1).
                    category(build).
                    build();

            Ad tux = Ad.builder().
                    title("Leier ut tux").
                    description("1000 kr pr kveld").
                    rental(true).
                    durationType(AdType.WEEK).
                    duration(2).
                    price(1000).
                    created(LocalDate.now()).
                    lat(62.4).
                    lng(10.4).
                    streetAddress("Project Road 5").
                    postalCode(7000).
                    city("Trondheim").
                    user(user2).
                    category(klor).
                    build();

            Ad pc = Ad.builder().
                    title("Leier ut Pc").
                    description("Renting ut en ny lenovo").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(800).
                    created(LocalDate.now()).
                    lat(64.4).
                    lng(10.4).
                    streetAddress("Project Road 6").
                    postalCode(7800).
                    city("Trondheim").
                    user(user3).
                    category(datamaskin).
                    build();

            Ad charger = Ad.builder().
                    title("Pc lader").
                    description("Leier ut en ny lenovo lader").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(1000).
                    created(LocalDate.now()).
                    lat(60.4).
                    lng(10.4).
                    streetAddress("Project Road 6").
                    postalCode(7800).
                    city("Trondheim").
                    user(user3).
                    category(lader).
                    build();
            Ad motherBoard = Ad.builder().
                    title("Mother board").
                    description("Leier ut ut ny lenovo motherboard").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(600).
                    created(LocalDate.now()).
                    lat(63.4).
                    lng(10.4).
                    streetAddress("Project Road 6").
                    postalCode(7800).
                    city("Trondheim").
                    user(user3).
                    category(datamaskin).
                    build();

            Ad sove = Ad.builder().
                    title("Sovepose og primus").
                    description("Leier ut sovepose og primus, leies ut kun sammen").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(300).
                    created(LocalDate.now()).
                    lat(60.9).
                    lng(10.4).
                    streetAddress("gata 4").
                    postalCode(7202).
                    user(user1).
                    category(tur).
                    build();

            Ad newHammer = Ad.builder().
                    title("Ny Hammer").
                    description("Leier ut en ny hammer").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(200).
                    created(LocalDate.now()).
                    lat(59.4).
                    lng(10.4).
                    streetAddress("Prosjekt P").
                    postalCode(7201).
                    user(user1).
                    category(hammer).
                    build();

            Ad matte = Ad.builder().
                    title("Skolebøker Matematikk 3").
                    description("Leier ut matematiske metoder 3 boka").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(100).
                    created(LocalDate.now()).
                    lat(63.4).
                    lng(11.4).
                    streetAddress("gate 42").
                    postalCode(7200).
                    user(user1).
                    category(skole).
                    build();

            Ad klovn = Ad.builder().
                    title("Klovnekostyme").
                    description("Leier ut ett klovne-sett").
                    rental(true).
                    durationType(AdType.MONTH).
                    duration(2).
                    price(300).
                    created(LocalDate.now()).
                    lat(63.4).
                    lng(10.4).
                    streetAddress("Klovnegata").
                    postalCode(7200).
                    user(user3).
                    category(klor).
                    build();

            Ad tent = Ad.builder().
                    title("Nytt telt").
                    description("Lavvo med plass til 8").
                    rental(true).
                    durationType(AdType.DAY).
                    duration(2).
                    price(800).
                    created(LocalDate.now()).
                    lat(63.4).
                    lng(12.4).
                    streetAddress("Project 4").
                    postalCode(7200).
                    user(user4).
                    category(tur).
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

            adRepository.save(borre);
            adRepository.save(tux);
            adRepository.save(pc);
            adRepository.save(charger);
            adRepository.save(motherBoard);


            adRepository.save(sove);
            adRepository.save(newHammer);
            adRepository.save(matte);
            adRepository.save(klovn);
            adRepository.save(tent);

            Rental rental = Rental.builder()
                    .ad(borre)
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
                    .ad(klovn)
                    .owner(user1)
                    .borrower(user2)
                    .price(1000)
                    .active(true)
                    .deadline(LocalDate.now().plusDays(3))
                    .rentTo(LocalDate.now().plusDays(7))
                    .rentFrom(LocalDate.now().plusDays(4))
                    .dateOfRental(LocalDate.now())
                    .build();
            rentalRepository.save(rental);

            rental = Rental.builder()
                    .ad(matte)
                    .owner(user1)
                    .borrower(user2)
                    .price(100)
                    .active(true)
                    .deadline(LocalDate.now().plusDays(5))
                    .rentTo(LocalDate.now().plusDays(9))
                    .rentFrom(LocalDate.now().plusDays(6))
                    .dateOfRental(LocalDate.now())
                    .build();
            rentalRepository.save(rental);

            rental = Rental.builder()
                    .ad(newHammer)
                    .owner(user3)
                    .borrower(user1)
                    .price(3000)
                    .active(false)
                    .deadline(LocalDate.now().plusDays(7))
                    .rentTo(LocalDate.now().plusDays(12))
                    .rentFrom(LocalDate.now().plusDays(8))
                    .dateOfRental(LocalDate.now())
                    .build();
            rentalRepository.save(rental);

            rental = Rental.builder()
                    .ad(sove)
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
                    .ad(borre)
                    .user(user3)
                    .description("veldig bra anbefaler dette produktet!")
                    .rating(9)
                    .build();
            reviewRepository.save(review);

            review = Review.builder()
                    .ad(borre)
                    .user(user2)
                    .description("Elendig produkt")
                    .rating(6)
                    .build();
            reviewRepository.save(review);

            review = Review.builder()
                    .ad(borre)
                    .user(user3)
                    .description("ten out of ten would buy again")
                    .rating(6)
                    .build();
            reviewRepository.save(review);

            review = Review.builder()
                    .ad(borre)
                    .user(user4)
                    .description("two out of ten would never buy again")
                    .rating(1)
                    .build();
            reviewRepository.save(review);

            review = Review.builder()
                    .ad(matte)
                    .user(user2)
                    .description("Elendig produkt")
                    .rating(6)
                    .build();
            reviewRepository.save(review);

            review = Review.builder()
                    .ad(klovn)
                    .user(user3)
                    .description("ten out of ten would buy again")
                    .rating(6)
                    .build();
            reviewRepository.save(review);

            review = Review.builder()
                    .ad(newHammer)
                    .user(user4)
                    .description("two out of ten would never buy again")
                    .rating(1)
                    .build();
            reviewRepository.save(review);

            review = Review.builder()
                    .ad(sove)
                    .user(user2)
                    .description("Elendig produkt")
                    .rating(6)
                    .build();
            reviewRepository.save(review);

            review = Review.builder()
                    .ad(pc)
                    .user(user2)
                    .description("ten out of ten would buy again")
                    .rating(6)
                    .build();
            reviewRepository.save(review);

            review = Review.builder()
                    .ad(charger)
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
            ads1.add(tux);
            telt.setAds(ads1);

            Set<Ad> ads2 = new HashSet<>();
            ads2.add(borre);
            build.setAds(ads2);

            Set<Ad> ads3 = new HashSet<>();
            ads3.add(pc);
            datamaskin.setAds(ads3);

            categoryRepository.save(telt);
            categoryRepository.save(build);
            categoryRepository.save(datamaskin);
            adRepository.saveAll(ads);


            File file = new File("src/main/resources/static/images/borrmaskin.jpg");
            byte[] fileContent = Files.readAllBytes(file.toPath());
            Picture picture = Picture.builder()
                    .filename(file.getName())
                    .data(fileContent)
                    .type(Files.probeContentType(file.toPath()))
                    .build();
            borre.setPictures(new HashSet<>());
            borre.getPictures().add(picture);
            picture.setAd(borre);
            adRepository.save(borre);
            pictureRepository.save(picture);

            file = new File("src/main/resources/static/images/anders.jpg");
            fileContent = Files.readAllBytes(file.toPath());
            picture = Picture.builder()
                    .filename(file.getName())
                    .data(fileContent)
                    .type(Files.probeContentType(file.toPath()))
                    .build();

            user1.setPicture(picture);
            picture.setUser(user1);
            userRepository.save(user1);
            pictureRepository.save(picture);

            file = new File("src/main/resources/static/images/hammer.jpg");
            fileContent(newHammer, file);

            file = new File("src/main/resources/static/images/klovn.jpg");
            fileContent(klovn, file);

            file = new File("src/main/resources/static/images/lader.jpg");
            fileContent(charger, file);

            file = new File("src/main/resources/static/images/lavvo.jpg");
            fileContent(tent, file);

            file = new File("src/main/resources/static/images/lenovo.jpg");
            fileContent(pc, file);

            file = new File("src/main/resources/static/images/matte.jpg");
            fileContent(matte, file);

            file = new File("src/main/resources/static/images/mboard.jpg");
            fileContent(motherBoard, file);

            file = new File("src/main/resources/static/images/sovepose.jpg");
            fileContent(sove, file);

            file = new File("src/main/resources/static/images/tux.jpg");
            fileContent(tux, file);
        }

    private void fileContent(Ad newHammer, File file) throws IOException {
        byte[] fileContent;
        Picture picture;
        fileContent = Files.readAllBytes(file.toPath());
        picture = Picture.builder()
                .filename(file.getName())
                .data(fileContent)
                .type(Files.probeContentType(file.toPath()))
                .build();
        newHammer.setPictures(new HashSet<>());
        newHammer.getPictures().add(picture);
        picture.setAd(newHammer);
        adRepository.save(newHammer);
        pictureRepository.save(picture);
    }
}
