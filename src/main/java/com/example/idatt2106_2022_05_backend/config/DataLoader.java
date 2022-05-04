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

import java.sql.Timestamp;
import java.time.Instant;
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

    private GroupRepository groupRepository;

    private MessageRepository messageRepository;

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
     * @param groupRepository repository of the {@link Group} object
     * @param messageRepository repository of the {@link Message} object
     */
    public DataLoader(UserRepository userRepository, AdRepository adRepository,
                      CategoryRepository categoryRepository, CalendarDateRepository calDateRepository,
                      RentalRepository rentalRepository, ReviewRepository reviewRepository,
                      PictureRepository pictureRepository, GroupRepository groupRepository, MessageRepository messageRepository) {

            this.userRepository = userRepository;
            this.adRepository = adRepository;
            this.categoryRepository = categoryRepository;
            this.calDateRepository = calDateRepository;
            this.rentalRepository = rentalRepository;
            this.reviewRepository = reviewRepository;
            this.pictureRepository = pictureRepository;
            this.groupRepository = groupRepository;
            this.messageRepository = messageRepository;
            this.groupRepository = groupRepository;
            this.messageRepository = messageRepository;
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
            Category kitchen = Category.builder().name("Kjøkken").parent(true).icon("fa-kitchen-set").build();
            Category vehicle = Category.builder().name("Kjøretøy").parent(true).icon("fa-car-side").build();
            Category sport = Category.builder().name("Sport").parent(true).icon("fa-baseball-bat-ball").build();
            Category computer = Category.builder().name("Data og datautstyr").parent(true).icon("fa-computer").build();
            Category sound = Category.builder().name("Lyd").icon("fa-volume-high").parent(true).build();
            Category instruments = Category.builder().name("Instrumenter").parent(true).icon("fa-guitar").build();
            Category clothing = Category.builder().name("Klær og tilbehør").parent(true).icon("fa-shirt").build();
            Category hobby = Category.builder().name("Hobby og fritid").parent(true).icon("fa-paintbrush").build();
            Category garden = Category.builder().name("Hage, oppussing og møbler").parent(true).icon("fa-house-chimney").build();

            //persisting categories
            categoryRepository.save(kitchen);
            categoryRepository.save(vehicle);
            categoryRepository.save(sport);
            categoryRepository.save(sport);
            categoryRepository.save(computer);
            categoryRepository.save(sound);
            categoryRepository.save(instruments);
            categoryRepository.save(clothing);
            categoryRepository.save(hobby);
            categoryRepository.save(garden);

            System.out.println("categories: " + categoryRepository.findAll());

            // Create sub-categories kitchen
            Category kitchenmachine = Category.builder().name("Kjøkkenmaskin").
                    parentName(kitchen.getName()).build();
            Category grill = Category.builder().name("Grill").
                    parentName(kitchen.getName()).build();
            Category pizzaovn = Category.builder().name("Pizzaovn").
                    parentName(kitchen.getName()).build();
            Category otherKitchen = Category.builder().name("Annet").
                    parentName(kitchen.getName()).build();
            categoryRepository.save(kitchenmachine);
            categoryRepository.save(grill);
            categoryRepository.save(pizzaovn);
            categoryRepository.save(otherKitchen);

            //Sub-categories for vehicle
            Category car = Category.builder().name("Bil").
                    parentName(vehicle.getName()).build();
            Category boat = Category.builder().name("Båt").
                    parentName(vehicle.getName()).build();
            Category bike = Category.builder().name("Sykkel").
                    parentName(vehicle.getName()).build();
            Category bikey = Category.builder().name("Sparkesykkel").
                    parentName(vehicle.getName()).build();
            Category scooter = Category.builder().name("Moped").
                    parentName(vehicle.getName()).build();
            Category hanger = Category.builder().name("Tilhenger").
                    parentName(vehicle.getName()).build();
            Category digger = Category.builder().name("Gravemaskin").
                    parentName(vehicle.getName()).build();
            Category otherVehicle = Category.builder().name("Annet").
                    parentName(vehicle.getName()).build();
            categoryRepository.save(car);
            categoryRepository.save(boat);
            categoryRepository.save(bike);
            categoryRepository.save(bikey);
            categoryRepository.save(scooter);
            categoryRepository.save(hanger);
            categoryRepository.save(digger);
            categoryRepository.save(otherVehicle);

            //Sub-categories for sport
            Category ballSport = Category.builder().name("Ballsport").parentName(sport.getName()).build();
            Category waterSport = Category.builder().name("Vannsport").parentName(sport.getName()).build();
            Category skis = Category.builder().name("Ski og skiutstyr").parentName(sport.getName()).build();
            Category outdoorlife = Category.builder().name("Friluftsliv").parentName(sport.getName()).build();
            categoryRepository.save(ballSport);
            categoryRepository.save(waterSport);
            categoryRepository.save(skis);
            categoryRepository.save(outdoorlife);

            //Sub-categories for ballsport
            Category handball = Category.builder().name("Håndball").parentName(ballSport.getName()).build();
            Category fotball = Category.builder().name("Fotball").parentName(ballSport.getName()).build();
            Category basket = Category.builder().name("Basketball").parentName(ballSport.getName()).build();
            Category ballSportother = Category.builder().name("Annet").parentName(ballSport.getName()).build();
            categoryRepository.save(handball);
            categoryRepository.save(fotball);
            categoryRepository.save(basket);
            categoryRepository.save(ballSportother);

            //Sub-categories for watersport
            Category lifewest = Category.builder().name("Redningsvest").parentName(waterSport.getName()).build();
            Category divingequipment = Category.builder().name("Dykkerutstyr").parentName(waterSport.getName()).build();
            Category wetsuit = Category.builder().name("Våtdrakt").parentName(waterSport.getName()).build();
            Category badedyre = Category.builder().name("Badedyr").parentName(waterSport.getName()).build();
            Category parasail = Category.builder().name("Parasail").parentName(waterSport.getName()).build();
            Category tube = Category.builder().name("Tube").parentName(waterSport.getName()).build();
            Category paddle = Category.builder().name("Paddlebrett").parentName(waterSport.getName()).build();
            Category waterski =Category.builder().name("Vannski").parentName(waterSport.getName()).build();
            categoryRepository.save(lifewest);
            categoryRepository.save(divingequipment);
            categoryRepository.save(badedyre);
            categoryRepository.save(wetsuit);
            categoryRepository.save(parasail);
            categoryRepository.save(tube);
            categoryRepository.save(paddle);
            categoryRepository.save(waterski);

            //sub-categories for ski
            Category slalomski = Category.builder().name("Slalomski").parentName(skis.getName()).build();
            Category slalomsko = Category.builder().name("Slalomsko").parentName(skis.getName()).build();
            Category slalomstaver = Category.builder().name("Slalomstaver").parentName(skis.getName()).build();
            Category helmet = Category.builder().name("Hjelm").parentName(skis.getName()).build();
            Category skoyteski = Category.builder().name("Skøyteski").parentName(skis.getName()).build();
            Category klassiske = Category.builder().name("Klassiske ski").parentName(skis.getName()).build();
            Category fellesski = Category.builder().name("Felleski").parentName(skis.getName()).build();
            Category otherski = Category.builder().name("Annet").parentName(skis.getName()).build();
            categoryRepository.save(slalomski);
            categoryRepository.save(slalomsko);
            categoryRepository.save(slalomstaver);
            categoryRepository.save(helmet);
            categoryRepository.save(skoyteski);
            categoryRepository.save(klassiske);
            categoryRepository.save(fellesski);
            categoryRepository.save(fellesski);
            categoryRepository.save(otherski);

            //sub-categories for outdoor living
            Category telt = Category.builder().name("Telt").parentName(outdoorlife.getName()).build();
            Category fishingequipment = Category.builder().name("Fiskeutstyr").parentName(outdoorlife.getName()).build();
            Category kano = Category.builder().name("Kano").parentName(outdoorlife.getName()).build();
            Category kajakk  = Category.builder().name("Kajakk").parentName(outdoorlife.getName()).build();
            Category clohting = Category.builder().name("Ytterklær").parentName(outdoorlife.getName()).build();
            Category otherOutdoor = Category.builder().name("Annte").parentName(outdoorlife.getName()).build();
            categoryRepository.save(telt);
            categoryRepository.save(fishingequipment);
            categoryRepository.save(kajakk);
            categoryRepository.save(kano);
            categoryRepository.save(clohting);
            categoryRepository.save(otherOutdoor);

            //sub-categories data
            Category screen = Category.builder().name("Skjerm").parentName(computer.getName()).build();
            Category datamaskin = Category.builder().name("Datamaskin").parentName(computer.getName()).build();
            Category spill =Category.builder().name("Spill").parentName(computer.getName()).build();
            Category spillkonsoll = Category.builder().name("Spillkonsoll").parentName(computer.getName()).build();
            Category kabler  = Category.builder().name("Ledninger").parentName(computer.getName()).build();
            Category mobil =Category.builder().name("Mobil").parentName(computer.getName()).build();
            Category kampera = Category.builder().name("Kamera").parentName(computer.getName()).build();
            Category otherComputer =Category.builder().name("Annet").parentName(computer.getName()).build();
            categoryRepository.save(screen);
            categoryRepository.save(datamaskin);
            categoryRepository.save(spill);
            categoryRepository.save(spillkonsoll);
            categoryRepository.save(kabler);
            categoryRepository.save(mobil);
            categoryRepository.save(kampera);
            categoryRepository.save(otherComputer);

            //Sub-categories sound
            Category speaker = Category.builder().name("Høytaler").parentName(sound.getName()).build();
            Category cd = Category.builder().name("CD-spiller").parentName(sound.getName()).build();
            Category lp = Category.builder().name("Platespiller").parentName(sound.getName()).build();
            Category headphones = Category.builder().name("Hodetelefoner").parentName(sound.getName()).build();
            Category othersound = Category.builder().name("Annet").parentName(sound.getName()).build();
            categoryRepository.save(speaker);
            categoryRepository.save(cd);
            categoryRepository.save(lp);
            categoryRepository.save(headphones);
            categoryRepository.save(othersound);

            //Other instrument
            Category blow = Category.builder().name("Blåseinstrument").parentName(instruments.getName()).build();
            Category streng = Category.builder().name("Strengeinstrument").parentName(instruments.getName()).build();
            Category elektronisk = Category.builder().name("Elektriske instrumenter").parentName(instruments.getName()).build();
            Category slag = Category.builder().name("Slaginstrument").parentName(instruments.getName()).build();
            Category otherinstrument = Category.builder().name("Annet").parentName(instruments.getName()).build();
            categoryRepository.save(blow);
            categoryRepository.save(streng);
            categoryRepository.save(elektronisk);
            categoryRepository.save(slag);
            categoryRepository.save(otherinstrument);

            //sub-categories for Clothes
            Category clothes = Category.builder().name("Klær").parentName(clohting.getName()).build();
            Category veske = Category.builder().name("Vesker").parentName(clothing.getName()).build();
            Category sko = Category.builder().name("Sko").parentName(clothing.getName()).build();
            Category smykker = Category.builder().name("Smykker").parentName(clothing.getName()).build();
            Category otherclothes = Category.builder().name("Annet").parentName(clohting.getName()).build();
            categoryRepository.save(clothes);
            categoryRepository.save(veske);
            categoryRepository.save(smykker);
            categoryRepository.save(sko);
            categoryRepository.save(otherclothes);

            //Sub-categories for hobby
            Category brettspill = Category.builder().name("Brettspill").parentName(hobby.getName()).build();
            Category books = Category.builder().name("Bøker").parentName(hobby.getName()).build();
            Category sklie = Category.builder().name("Sklie").parentName(hobby.getName()).build();
            Category hoppeslott = Category.builder().name("Hoppeslott").parentName(hobby.getName()).build();
            Category pynt = Category.builder().name("Pyntegjenstander").parentName(hobby.getName()).build();
            Category otherhobby = Category.builder().name("Annet").parentName(hobby.getName()).build();
            categoryRepository.save(brettspill);
            categoryRepository.save(books);
            categoryRepository.save(sklie);
            categoryRepository.save(hoppeslott);
            categoryRepository.save(pynt);
            categoryRepository.save(otherhobby);

            //sub-categories for gardening
            Category verktoy = Category.builder().name("Verktøy").parentName(garden.getName()).build();
            Category container = Category.builder().name("Container").parentName(garden.getName()).build();
            Category stoler = Category.builder().name("Stoler og bord").parentName(garden.getName()).build();
            Category hage = Category.builder().name("Hage og uteliv").parentName(garden.getName()).build();
            Category othergarden = Category.builder().name("Annet").parentName(garden.getName()).build();
            categoryRepository.save(verktoy);
            categoryRepository.save(container);
            categoryRepository.save(stoler);
            categoryRepository.save(hage);
            categoryRepository.save(othergarden);

            //sub-categories for hage
            Category trillebor = Category.builder().name("Trillebår").parentName(hage.getName()).build();
            Category hageverktoy = Category.builder().name("Hageredskaper").parentName(hage.getName()).build();
            categoryRepository.save(trillebor);
            categoryRepository.save(hageverktoy);

            // Create ad
            Ad pants = Ad.builder().
                    title("Borre-maskin").
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
                    category(verktoy).
                    build();

            Ad fruit = Ad.builder().
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
                    category(clothes).
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
                    category(datamaskin).
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
                    category(otherOutdoor).
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
                    category(verktoy).
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
                    category(books).
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
                    category(otherhobby).
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
                    category(otherOutdoor).
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
            ads2.add(pants);
            verktoy.setAds(ads2);

            Set<Ad> ads3 = new HashSet<>();
            ads3.add(pc);
            datamaskin.setAds(ads3);

            categoryRepository.save(telt);
            categoryRepository.save(verktoy);
            categoryRepository.save(datamaskin);
            adRepository.saveAll(ads);

            File file = new File("src/main/resources/images/pants.jpg");
            byte[] fileContent = Files.readAllBytes(file.toPath());
            Picture picture = Picture.builder()
                    .filename(file.getName())
                    .data(fileContent)
                    .type(Files.probeContentType(file.toPath()))
                    .build();

            user1.setPicture(picture);
            picture.setUser(user1);
            userRepository.save(user1);
            pictureRepository.save(picture);

//            File file = new File("src/main/resources/static/images/random/austin-chan-ukzHlkoz1IE-unsplash.jpg");
//            FileInputStream input = new FileInputStream(file);
//            MultipartFile multipartFile = new MockMultipartFile("file",
//                    file. getName(), "image/jpg", (input).readAllBytes());
//            Set<Picture> set = new HashSet<>();
//            Picture pic = Picture.builder()
//                    .ad(pants)
//                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
//                    .filename(file.getName())
//                    .type("jpg")
//                    .build();
//            set.add(pic);
//            pants.setPictures(new HashSet<>());
//            pants.setPictures(set);
//            pictureRepository.save(pic);
//            adRepository.save(pants);
//
//
//
//            file = new File("src/main/resources/static/images/random/bekir-donmez-eofm5R5f9Kw-unsplash.jpg");
//            input = new FileInputStream(file);
//            multipartFile = new MockMultipartFile("file",
//                    file. getName(), "image/jpg", (input).readAllBytes());
//            set = new HashSet<>();
//            pic = Picture.builder()
//                    .ad(pants)
//                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
//                    .filename(file.getName())
//                    .type("jpg")
//                    .build();
//            set.add(pic);
//            pants.setPictures(new HashSet<>());
//            pants.setPictures(set);
//            pictureRepository.save(pic);
//            adRepository.save(pants);
//
//            file = new File("src/main/resources/static/images/random/david-kovalenko-G85VuTpw6jg-unsplash.jpg");
//            input = new FileInputStream(file);
//            multipartFile = new MockMultipartFile("file",
//                    file. getName(), "image/jpg", (input).readAllBytes());
//            set = new HashSet<>();
//            pic = Picture.builder()
//                    .ad(pants)
//                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
//                    .filename(file.getName())
//                    .type("jpg")
//                    .build();
//            set.add(pic);
//            pants.setPictures(new HashSet<>());
//            pants.setPictures(set);
//            pictureRepository.save(pic);
//            adRepository.save(pants);
//
//            file = new File("src/main/resources/static/images/random/diego-ph-fIq0tET6llw-unsplash.jpg");
//            input = new FileInputStream(file);
//            multipartFile = new MockMultipartFile("file",
//                    file. getName(), "image/jpg", (input).readAllBytes());
//            set = new HashSet<>();
//            pic = Picture.builder()
//                    .ad(pants)
//                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
//                    .filename(file.getName())
//                    .type("jpg")
//                    .build();
//            set.add(pic);
//            pants.setPictures(new HashSet<>());
//            pants.setPictures(set);
//            pictureRepository.save(pic);
//            adRepository.save(pants);
//
//
//            file = new File("src/main/resources/static/images/random/ian-dooley-hpTH5b6mo2s-unsplash.jpg");
//            input = new FileInputStream(file);
//            multipartFile = new MockMultipartFile("file",
//                    file. getName(), "image/jpg", (input).readAllBytes());
//            set = new HashSet<>();
//            pic = Picture.builder()
//                    .ad(fruit)
//                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
//                    .filename(file.getName())
//                    .type("jpg")
//                    .build();
//            set.add(pic);
//            fruit.setPictures(new HashSet<>());
//            fruit.setPictures(set);
//            pictureRepository.save(pic);
//            adRepository.save(pants);
//
//
//            file = new File("src/main/resources/static/images/random/kristopher-roller-PC_lbSSxCZE-unsplash.jpg");
//            input = new FileInputStream(file);
//            multipartFile = new MockMultipartFile("file",
//                    file. getName(), "image/jpg", (input).readAllBytes());
//            set = new HashSet<>();
//            pic = Picture.builder()
//                    .ad(fruit)
//                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
//                    .filename(file.getName())
//                    .type("jpg")
//                    .build();
//            set.add(pic);
//            fruit.setPictures(new HashSet<>());
//            fruit.setPictures(set);
//            pictureRepository.save(pic);
//            adRepository.save(pants);
//
//
//            file = new File("src/main/resources/static/images/random/diego-ph-fIq0tET6llw-unsplash.jpg");
//            input = new FileInputStream(file);
//            multipartFile = new MockMultipartFile("file",
//                    file. getName(), "image/jpg", (input).readAllBytes());
//            set = new HashSet<>();
//            pic = Picture.builder()
//                    .ad(fruit)
//                    .data(PictureUtility.compressImage(multipartFile.getBytes()))
//                    .filename(file.getName())
//                    .type("jpg")
//                    .build();
//            set.add(pic);
//            fruit.setPictures(new HashSet<>());
//            fruit.setPictures(set);
//            pictureRepository.save(pic);
//            adRepository.save(pants);

//            Ad ad =
//
//            pants.setDates(calendarService.addFutureDates(savedAd.getId()));
//            pants.setDates(calendarService.addFutureDates(savedAd.getId()));
//            pants.setDates(calendarService.addFutureDates(savedAd.getId()));
//            pants.setDates(calendarService.addFutureDates(savedAd.getId()));

            Group group1 = Group.builder()
                    .name("gruppechat1")
                    .build();

            Group group2 = Group.builder()
                    .name("gruppechat2")
                    .build();

            Group group3 = Group.builder()
                    .name("gruppechat3")
                    .build();

            Set<User> users1 = new HashSet<>();
            users1.add(user1);
            users1.add(user2);
            group1.setUsers(users1);

            Set<User> users2 = new HashSet<>();
            users2.add(user1);
            users2.add(user3);
            group2.setUsers(users2);

            Set<User> users3 = new HashSet<>();
            users3.add(user3);
            users3.add(user4);
            group3.setUsers(users3);

            groupRepository.save(group1);
            groupRepository.save(group2);
            groupRepository.save(group3);

            Message message1 = Message.builder()
                    .content("Hei!")
                    .group(group1)
                    .user(user1)
                    .timestamp(Timestamp.from(Instant.now()))
                    .build();

            Message message2 = Message.builder()
                    .content("Halo")
                    .group(group1)
                    .user(user2)
                    .timestamp(Timestamp.from(Instant.now()))
                    .build();

            Message message3 = Message.builder()
                    .content("Så fint vær idag.")
                    .group(group1)
                    .user(user2)
                    .timestamp(Timestamp.from(Instant.now()))
                    .build();

            Message message4 = Message.builder()
                    .content("Nei")
                    .group(group1)
                    .user(user1)
                    .timestamp(Timestamp.from(Instant.now()))
                    .build();

            Message message5 = Message.builder()
                    .content("-(^__^)-")
                    .group(group2)
                    .user(user3)
                    .timestamp(Timestamp.from(Instant.now()))
                    .build();

            messageRepository.save(message1);
            messageRepository.save(message2);
            messageRepository.save(message3);
            messageRepository.save(message4);
            messageRepository.save(message5);

        }
}
