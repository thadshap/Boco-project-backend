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
import java.util.Date;
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
            User user1 = User.builder().numberOfReviews(3).rating(8.2).firstName("Anders").lastName("Tellefsen").email("andetel@stud.ntnu.no").password(passwordEncoder.encode("passord123")).verified(true).role("User").build();
            User user2 = User.builder().numberOfReviews(4).rating(3.1).firstName("Brage").lastName("Minge").email("bragem@stud.ntnu.no").password(passwordEncoder.encode("passord123")).verified(true).role("User").build();
            User user3 = User.builder().numberOfReviews(100).rating(9).firstName("Hasan").lastName("Rehman").email("hasano@stud.ntnu.no").password(passwordEncoder.encode("passord123")).verified(true).role("User").build();
            User user4 = User.builder().numberOfReviews(860).rating(10).firstName("Maiken Louise").lastName("Brechan").email("maiken@gmail.com").password(passwordEncoder.encode("passord123")).verified(true).role("User").build();
            User user5 = User.builder().numberOfReviews(3).rating(9.0).firstName("Thadsha").lastName("Paramsothy").email("thadsha@gmail.com").password(passwordEncoder.encode("passord123")).verified(true).role("User").build();
            User user6 = User.builder().numberOfReviews(1200).rating(6).firstName("Karoline").lastName("Wahl").email("karoline@gmail.com").password(passwordEncoder.encode("passord123")).verified(true).role("User").build();
            User user7 = User.builder().numberOfReviews(12).rating(1).firstName("Eirin").lastName("Svinsås").email("eirin@gmail.com").password(passwordEncoder.encode("passord123")).verified(true).role("User").build();
            User user8 = User.builder().numberOfReviews(60).rating(5).firstName("Leo").lastName("Leosen").email("leo@gmail.com").password(passwordEncoder.encode("passord123")).verified(true).role("User").build();
            User user9 = User.builder().numberOfReviews(2).rating(7).firstName("Johannes").lastName("Herman").email("johannes@gmail.com").password(passwordEncoder.encode("passord123")).verified(true).role("User").build();
            // Persist the users

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(user4);
            userRepository.save(user5);
            userRepository.save(user6);
            userRepository.save(user7);
            userRepository.save(user8);
            userRepository.save(user9);

            // Create main-categories
            Category kitchen = Category.builder().name("Kjøkken").parent(true).icon("fa fa-cutlery").build();
            Category vehicle = Category.builder().name("Kjøretøy").parent(true).icon("fa fa-car").build();
            Category sport = Category.builder().name("Sport").parent(true).icon("fas fa-biking").build();
            Category computer = Category.builder().name("Data og datautstyr").parent(true).icon("fa fa-laptop").build();
            Category sound = Category.builder().name("Lyd").icon("fa fa-volume-up").parent(true).build();
            Category instruments = Category.builder().name("Instrumenter").parent(true).icon("fa fa-guitar").build();
            Category clothing = Category.builder().name("Klær og tilbehør").parent(true).icon("fas fa-tshirt").build();
            Category hobby = Category.builder().name("Hobby og fritid").parent(true).icon("fas fa-running").build();
            Category garden = Category.builder().name("Hage, oppussing og møbler").parent(true).icon("fa fa-home").build();

            //persisting categories
            categoryRepository.save(kitchen);
            categoryRepository.save(vehicle);
            categoryRepository.save(sport);
            categoryRepository.save(computer);
            categoryRepository.save(sound);
            categoryRepository.save(instruments);
            categoryRepository.save(clothing);
            categoryRepository.save(hobby);
            categoryRepository.save(garden);

            // Create sub-categories kitchen
            Category kitchenmachine = Category.builder().name("Kjøkkenmaskin").parentName(kitchen.getName()).child(true).build();
            Category grill = Category.builder().child(true).name("Grill").parentName(kitchen.getName()).build();
            Category pizzaovn = Category.builder().child(true).name("Pizzaovn").parentName(kitchen.getName()).build();
            Category otherKitchen = Category.builder().name("Annet").parentName(kitchen.getName()).child(true).build();
            categoryRepository.save(kitchenmachine);
            categoryRepository.save(grill);
            categoryRepository.save(pizzaovn);
            categoryRepository.save(otherKitchen);

            //Sub-categories for vehicle
            Category car = Category.builder().name("Bil").child(true).parentName(vehicle.getName()).build();
            Category boat = Category.builder().name("Båt").child(true).parentName(vehicle.getName()).build();
            Category bike = Category.builder().name("Sykkel").child(true).parentName(vehicle.getName()).build();
            Category bikey = Category.builder().name("Sparkesykkel").child(true).parentName(vehicle.getName()).build();
            Category scooter = Category.builder().name("Moped").child(true).parentName(vehicle.getName()).build();
            Category hanger = Category.builder().name("Tilhenger").child(true).parentName(vehicle.getName()).build();
            Category digger = Category.builder().name("Gravemaskin").child(true).parentName(vehicle.getName()).build();
            Category otherVehicle = Category.builder().name("Annet").child(true).parentName(vehicle.getName()).build();
            categoryRepository.save(car);
            categoryRepository.save(boat);
            categoryRepository.save(bike);
            categoryRepository.save(bikey);
            categoryRepository.save(scooter);
            categoryRepository.save(hanger);
            categoryRepository.save(digger);
            categoryRepository.save(otherVehicle);

            //Sub-categories for sport
            Category ballSport = Category.builder().name("Ballsport").child(true).parent(true).parentName(sport.getName()).build();
            Category waterSport = Category.builder().name("Vannsport").child(true).parent(true).parentName(sport.getName()).build();
            Category skis = Category.builder().name("Ski og skiutstyr").child(true).parent(true).parentName(sport.getName()).build();
            Category outdoorlife = Category.builder().name("Friluftsliv").child(true).parent(true).parentName(sport.getName()).build();
            categoryRepository.save(ballSport);
            categoryRepository.save(waterSport);
            categoryRepository.save(skis);
            categoryRepository.save(outdoorlife);

            //Sub-categories for ballsport
            Category handball = Category.builder().name("Håndball").child(true).parentName(ballSport.getName()).build();
            Category fotball = Category.builder().name("Fotball").child(true).parentName(ballSport.getName()).build();
            Category basket = Category.builder().name("Basketball").child(true).parentName(ballSport.getName()).build();
            Category ballSportother = Category.builder().name("Annet").child(true).parentName(ballSport.getName()).build();
            categoryRepository.save(handball);
            categoryRepository.save(fotball);
            categoryRepository.save(basket);
            categoryRepository.save(ballSportother);

            //Sub-categories for watersport
            Category lifewest = Category.builder().name("Redningsvest").child(true).parentName(waterSport.getName()).build();
            Category divingequipment = Category.builder().name("Dykkerutstyr").child(true).parentName(waterSport.getName()).build();
            Category wetsuit = Category.builder().name("Våtdrakt").child(true).parentName(waterSport.getName()).build();
            Category badedyre = Category.builder().name("Badedyr").child(true).parentName(waterSport.getName()).build();
            Category parasail = Category.builder().name("Parasail").child(true).parentName(waterSport.getName()).build();
            Category tube = Category.builder().name("Tube").child(true).parentName(waterSport.getName()).build();
            Category paddle = Category.builder().name("Paddlebrett").child(true).parentName(waterSport.getName()).build();
            Category waterski =Category.builder().name("Vannski").child(true).parentName(waterSport.getName()).build();
            categoryRepository.save(lifewest);
            categoryRepository.save(divingequipment);
            categoryRepository.save(badedyre);
            categoryRepository.save(wetsuit);
            categoryRepository.save(parasail);
            categoryRepository.save(tube);
            categoryRepository.save(paddle);
            categoryRepository.save(waterski);

            //sub-categories for ski
            Category slalomski = Category.builder().name("Slalomski").child(true).parentName(skis.getName()).build();
            Category slalomsko = Category.builder().name("Slalomsko").child(true).parentName(skis.getName()).build();
            Category slalomstaver = Category.builder().name("Slalomstaver").child(true).parentName(skis.getName()).build();
            Category helmet = Category.builder().name("Hjelm").child(true).parentName(skis.getName()).build();
            Category skoyteski = Category.builder().name("Skøyteski").child(true).parentName(skis.getName()).build();
            Category klassiske = Category.builder().name("Klassiske ski").child(true).parentName(skis.getName()).build();
            Category fellesski = Category.builder().name("Felleski").child(true).parentName(skis.getName()).build();
            Category otherski = Category.builder().name("Annet").child(true).parentName(skis.getName()).build();
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
            Category telt = Category.builder().name("Telt").child(true).parentName(outdoorlife.getName()).build();
            Category fishingequipment = Category.builder().child(true).name("Fiskeutstyr").parentName(outdoorlife.getName()).build();
            Category kano = Category.builder().name("Kano").child(true).parentName(outdoorlife.getName()).build();
            Category kajakk  = Category.builder().name("Kajakk").child(true).parentName(outdoorlife.getName()).build();
            Category clohting = Category.builder().name("Ytterklær").child(true).parentName(outdoorlife.getName()).build();
            Category otherOutdoor = Category.builder().name("Annte").child(true).parentName(outdoorlife.getName()).build();
            categoryRepository.save(telt);
            categoryRepository.save(fishingequipment);
            categoryRepository.save(kajakk);
            categoryRepository.save(kano);
            categoryRepository.save(clohting);
            categoryRepository.save(otherOutdoor);

            //sub-categories data
            Category screen = Category.builder().name("Skjerm").child(true).parentName(computer.getName()).build();
            Category datamaskin = Category.builder().name("Datamaskin").child(true).parentName(computer.getName()).build();
            Category spill =Category.builder().name("Spill").child(true).parentName(computer.getName()).build();
            Category spillkonsoll = Category.builder().name("Spillkonsoll").child(true).parentName(computer.getName()).build();
            Category kabler  = Category.builder().name("Ledninger").child(true).parentName(computer.getName()).build();
            Category mobil =Category.builder().name("Mobil").child(true).parentName(computer.getName()).build();
            Category kampera = Category.builder().name("Kamera").child(true).parentName(computer.getName()).build();
            Category otherComputer =Category.builder().name("Annet").child(true).parentName(computer.getName()).build();
            categoryRepository.save(screen);
            categoryRepository.save(datamaskin);
            categoryRepository.save(spill);
            categoryRepository.save(spillkonsoll);
            categoryRepository.save(kabler);
            categoryRepository.save(mobil);
            categoryRepository.save(kampera);
            categoryRepository.save(otherComputer);

            //Sub-categories sound
            Category speaker = Category.builder().name("Høytaler").child(true).parentName(sound.getName()).build();
            Category cd = Category.builder().name("CD-spiller").child(true).parentName(sound.getName()).build();
            Category lp = Category.builder().name("Platespiller").child(true).parentName(sound.getName()).build();
            Category headphones = Category.builder().name("Hodetelefoner").child(true).parentName(sound.getName()).build();
            Category othersound = Category.builder().name("Annet").child(true).parentName(sound.getName()).build();
            categoryRepository.save(speaker);
            categoryRepository.save(cd);
            categoryRepository.save(lp);
            categoryRepository.save(headphones);
            categoryRepository.save(othersound);

            //Other instrument
            Category blow = Category.builder().name("Blåseinstrument").child(true).parentName(instruments.getName()).build();
            Category streng = Category.builder().name("Strengeinstrument").child(true).parentName(instruments.getName()).build();
            Category elektronisk = Category.builder().name("Elektriske instrumenter").child(true).parentName(instruments.getName()).build();
            Category slag = Category.builder().name("Slaginstrument").child(true).parentName(instruments.getName()).build();
            Category otherinstrument = Category.builder().name("Annet").child(true).parentName(instruments.getName()).build();
            categoryRepository.save(blow);
            categoryRepository.save(streng);
            categoryRepository.save(elektronisk);
            categoryRepository.save(slag);
            categoryRepository.save(otherinstrument);

            //sub-categories for Clothes
            Category clothes = Category.builder().name("Klær").child(true).parentName(clohting.getName()).build();
            Category veske = Category.builder().name("Vesker").child(true).parentName(clothing.getName()).build();
            Category sko = Category.builder().name("Sko").child(true).parentName(clothing.getName()).build();
            Category smykker = Category.builder().name("Smykker").child(true).parentName(clothing.getName()).build();
            Category otherclothes = Category.builder().name("Annet").child(true).parentName(clohting.getName()).build();
            categoryRepository.save(clothes);
            categoryRepository.save(veske);
            categoryRepository.save(smykker);
            categoryRepository.save(sko);
            categoryRepository.save(otherclothes);

            //Sub-categories for hobby
            Category brettspill = Category.builder().name("Brettspill").child(true).parentName(hobby.getName()).build();
            Category books = Category.builder().name("Bøker").child(true).parentName(hobby.getName()).build();
            Category sklie = Category.builder().name("Sklie").child(true).parentName(hobby.getName()).build();
            Category hoppeslott = Category.builder().name("Hoppeslott").child(true).parentName(hobby.getName()).build();
            Category pynt = Category.builder().name("Pyntegjenstander").child(true).parentName(hobby.getName()).build();
            Category otherhobby = Category.builder().name("Annet").child(true).parentName(hobby.getName()).build();
            categoryRepository.save(brettspill);
            categoryRepository.save(books);
            categoryRepository.save(sklie);
            categoryRepository.save(hoppeslott);
            categoryRepository.save(pynt);
            categoryRepository.save(otherhobby);

            //sub-categories for gardening
            Category verktoy = Category.builder().name("Verktøy").child(true).parentName(garden.getName()).build();
            Category container = Category.builder().name("Container").child(true).parentName(garden.getName()).build();
            Category stoler = Category.builder().name("Stoler og bord").child(true).parentName(garden.getName()).build();
            Category hage = Category.builder().name("Hage og uteliv").child(true).parent(true).parentName(garden.getName()).build();
            Category othergarden = Category.builder().name("Annet").child(true).parentName(garden.getName()).build();
            categoryRepository.save(verktoy);
            categoryRepository.save(container);
            categoryRepository.save(stoler);
            categoryRepository.save(hage);
            categoryRepository.save(othergarden);

            //sub-categories for hage
            Category trillebor = Category.builder().child(true).name("Trillebår").parentName(hage.getName()).build();
            Category hageverktoy = Category.builder().child(true).name("Hageredskaper").parentName(hage.getName()).build();
            categoryRepository.save(trillebor);
            categoryRepository.save(hageverktoy);

            // Create ad
            Ad borremaskin = Ad.builder().title("Borre-maskin").description("Leier ut en kraftig borremaskin. Pent brukt og fungerer som den skal.").
                    rental(true).user(user5).durationType(AdType.MONTH).price(50).created(LocalDate.now()).lat(63.2).lng(10.4).streetAddress("Valgrindvegen 5A").postalCode(7031).city("Trondheim").user(user1).category(verktoy).build();
            Ad tux = Ad.builder().title("Leier ut tux").description("1000 kr pr kveld").rental(true).durationType(AdType.WEEK).
                    price(1000).created(LocalDate.now()).lat(62.4).lng(10.1).streetAddress("Markaplassen 15").postalCode(7054).
                    city("Trondheim").user(user2).category(clothes).build();
            Ad pc = Ad.builder().title("Leier ut Pc").description("Lån en lenovo PC, funker bra til skole eller jobb.").rental(true).
                    durationType(AdType.MONTH).price(800).created(LocalDate.now()).lat(64.2).lng(10.4).streetAddress("Fagrabrekka 2").postalCode(7056).
                    city("Ranheim").user(user3).category(datamaskin).build();
            Ad charger = Ad.builder().title("Pc lader").description("Leier ut en ny lenovo lader").rental(true).durationType(AdType.MONTH).
                    price(1000).created(LocalDate.now()).lat(60.4).lng(10.4).streetAddress("Reidar Raaens veg 7").
                    postalCode(7056).city("Ranheim").user(user6).category(datamaskin).build();
            Ad motherBoard = Ad.builder().title("Mother board").description("Leier ut ut ny lenovo motherboard").
                    rental(true).durationType(AdType.MONTH).price(600).created(LocalDate.now()).lat(63.4).lng(10.4).
                    streetAddress("Svartholtet 12").postalCode(7092).city("Tiller").user(user7).category(datamaskin).build();
            Ad sovepose = Ad.builder().title("Sovepose og primus").description("Leier ut sovepose og primus, leies ut kun sammen").rental(true).
                    durationType(AdType.MONTH).price(300).created(LocalDate.now()).lat(60.9).lng(10.4).
                    streetAddress("Rognbudalen 18").postalCode(7092).city("Tiller").user(user8).category(otherOutdoor).build();
            Ad newHammer = Ad.builder().title("Ny Hammer").description("Leier ut en ny hammer").rental(true).user(user5).
                    durationType(AdType.MONTH).price(200).created(LocalDate.now()).lat(59.4).lng(10.4).
                    streetAddress("Arne Solbergs veg 30").postalCode(7092).city("Tiller").user(user9).category(verktoy).build();
            Ad matte = Ad.builder().title("Skolebøker Matematikk 3").user(user6).description("Leier ut matematiske metoder 3 boka").rental(true).
                    durationType(AdType.MONTH).price(100).created(LocalDate.now()).lat(63.4).lng(11.4).
                    streetAddress("Gabriel Scotts veg 32").postalCode(7023).city("Trondheim").user(user1).category(books).build();
            Ad klovn = Ad.builder().title("Klovnekostyme").description("Leier ut ett klovne-sett").rental(true).durationType(AdType.MONTH).price(300).created(LocalDate.now()).lat(63.4).lng(10.5).
                    streetAddress("Konrad Dahls veg 7B").city("Trondheim").postalCode(7024).user(user2).category(otherhobby).build();
            Ad tent = Ad.builder().title("Nytt telt").description("Lavvo med plass til 8").rental(true).durationType(AdType.DAY).
                    price(800).created(LocalDate.now()).lat(63.7).lng(12.4).streetAddress("Ålivegen 6C").city("Vikhammer").
                    postalCode(7560).user(user3).category(telt).build();
            // Persist all ads
            adRepository.save(borremaskin);
            adRepository.save(tux);
            adRepository.save(pc);
            adRepository.save(charger);
            adRepository.save(sovepose);
            adRepository.save(motherBoard);
            adRepository.save(newHammer);
            adRepository.save(matte);
            adRepository.save(klovn);
            adRepository.save(tent);

            Ad kjokkenmaskin = Ad.builder().description("Brødbakemaskin leies ut. Man kan bake alt fra pizza deig til dansk rugbrød.").title("Bosch Brødbakemaskin").durationType(AdType.WEEK).price(350).
                    postalCode(7054).streetAddress("Væretrøa 160").city("Ranheim").rental(true).user(user4).category(kitchenmachine).created(LocalDate.now()).lat(64.43).lng(10.4).build();
            Ad grillen = Ad.builder().title("Gassgrill").description("Gassgrill leies ut uten gasstank").durationType(AdType.MONTH).price(500).postalCode(7563).city("Malvik").streetAddress("Smiskaret 79").
                    rental(true).user(user5).category(grill).created(LocalDate.now()).lat(64.4).lng(10.3).build();
            Ad pizzaspade = Ad.builder().title("Pizzaspade").description("Pizzaspade gis bort da den ikke blir brukt.").postalCode(7550).city("Hommelvik").streetAddress("Steinbruddvegen 3").price(0).rental(false).
                    user(user6).lat(64.3).durationType(AdType.DAY).lng(10.4).created(LocalDate.now()).category(pizzaovn).build();
            Ad koleboks = Ad.builder().title("Kjøleboks leies ut").description("Kjøleboks leies ut til arrangementer").durationType(AdType.DAY).streetAddress("Fjordvegen 2").postalCode(9999).city("Båtsfjord").
                    price(100).user(user7).rental(true).category(otherKitchen).created(LocalDate.now()).lat(71.0).lng(60).build();
            adRepository.save(kjokkenmaskin);
            adRepository.save(grillen);
            adRepository.save(pizzaspade);
            adRepository.save(koleboks);

            Ad bil = Ad.builder().title("Bil leies ut").description("Leier ut en volvo 240 til 200kr dagen").durationType(AdType.DAY).postalCode(7500).streetAddress("Stokkanvegen 2").
                    price(200).user(user8).category(car).rental(true).city("Stjørdal").created(LocalDate.now()).lat(64.4).lng(10.4).build();
            Ad bot = Ad.builder().title("Seilbåt til utleie").streetAddress("Illsvikøra 11").description("Leier ut seilbåten min i skansen for dagsturer til erfarne seilere").durationType(AdType.DAY).
                    postalCode(7018).price(1200).user(user9).rental(true).category(boat).city("Trondheim").created(LocalDate.now()).lat(63.43).lng(10.36).build();
            Ad sykkel = Ad.builder().title("Sykkel til utleie").description("Leier ut bysykkelen min. Perfekt til turister.").durationType(AdType.DAY).postalCode(7020).streetAddress("Schnitlers vei 6").
                    price(100).user(user1).rental(true).city("Trondheim").category(bike).lat(63.42).lng(10.36).created(LocalDate.now()).build();
            Ad sparkesykkel = Ad.builder().description("Triksesparkesykkel kan leies for en billig penge").title("Trikse sparkesykkel").durationType(AdType.HOUR).postalCode(7042).city("Trondheim").
                    streetAddress("Biskop Sigurds gate 7").price(70).rental(true).user(user2).created(LocalDate.now()).category(bikey).lat(63.43).lng(10.42).build();
            Ad moped = Ad.builder().title("Moped til utleie").description("Leier ut mopeden min til daglig bruk, da den ikke brukes så mye.").durationType(AdType.DAY).postalCode(7014).city("Trondheim").
                    streetAddress("Øvre Kristianstens gate 2B").price(300).rental(true).user(user3).category(scooter).lat(63.42).lng(10.40).rentedOut(true).created(LocalDate.now()).build();
            Ad tilhenger = Ad.builder().title("Tilhenger").description("Stor tilhenger til utleie, funker for møbler og store gjenstander.").durationType(AdType.DAY).postalCode(7051).price(350).rental(true).
                    streetAddress("Øvre Bergsvingen 3").city("Trondheim").user(user4).created(LocalDate.now()).category(hanger).lat(63.41).lng(10.42).build();
            Ad gravemaskin = Ad.builder().title("Gravemaskin til utleie").description("Leier ut en Ultimate digger 2000 fra CAT.Leier må hente selv.").durationType(AdType.DAY).postalCode(7224).streetAddress("losjevegen 4").
                    city("Melhus").price(1100).user(user5).rental(true).created(LocalDate.now()).category(digger).lat(63.28).lng(10.28).build();
            Ad elsykker = Ad.builder().title("Min fine rosa elsykkel!").description("Vanskelig å komme seg hjem fra byen i helgene? Lån denne kjærra her!").durationType(AdType.DAY).price(600).postalCode(7036).
                    streetAddress("Venusvegen 10").city("Trondheim").rental(true).user(user6).created(LocalDate.now()).category(otherVehicle).lat( 63.39).lng(10.41).build();
            adRepository.save(bil);
            adRepository.save(bot);
            adRepository.save(sykkel);
            adRepository.save(moped);
            adRepository.save(sparkesykkel);
            adRepository.save(tilhenger);
            adRepository.save(gravemaskin);
            adRepository.save(elsykker);

            Ad handyball = Ad.builder().title("Håndballsko").description("Låner ut håndballskoene mine i str 38").durationType(AdType.WEEK).postalCode(7031).price(80).streetAddress("Valgrindvegen 5a").rental(true).user(user6).category(handball).city("Trondheim").created(LocalDate.now()).lat(64.3).lng(10.4).build();
            Ad ball = Ad.builder().title("Badeball").description("Mega badeball med diameter 30m").durationType(AdType.DAY).postalCode(7054).city("Ranheim").price(600).rental(true).streetAddress("Markaplassen 15").user(user4).created(LocalDate.now()).lat(63.4).lng(10.39).category(ballSport).build();
            adRepository.save(handyball);
            adRepository.save(ball);

            Rental rental = Rental.builder().rentFrom(LocalDate.now().plusDays(2)).dateOfRental(LocalDate.now()).build();
            rentalRepository.save(rental);
            rental = Rental.builder().ad(klovn).owner(user2).borrower(user8).price(1000).active(true).deadline(LocalDate.now().plusDays(3)).rentTo(LocalDate.now().plusDays(7)).rentFrom(LocalDate.now().plusDays(4)).dateOfRental(LocalDate.now()).build();
            rentalRepository.save(rental);
            rental = Rental.builder().ad(matte).owner(user6).borrower(user9).price(100).active(true).deadline(LocalDate.now().plusDays(5)).rentTo(LocalDate.now().plusDays(9)).rentFrom(LocalDate.now().plusDays(6)).dateOfRental(LocalDate.now()).build();
            rentalRepository.save(rental);
            rental = Rental.builder().ad(newHammer).owner(user2).borrower(user1).price(3000).active(false).deadline(LocalDate.now().plusDays(7)).rentTo(LocalDate.now().plusDays(12)).rentFrom(LocalDate.now().plusDays(8)).dateOfRental(LocalDate.now()).build();
            rentalRepository.save(rental);
            rental = Rental.builder().ad(sovepose).owner(user8).borrower(user4).price(3000).active(true).deadline(LocalDate.now().plusDays(9)).rentTo(LocalDate.now().plusDays(15)).rentFrom(LocalDate.now().plusDays(10)).dateOfRental(LocalDate.now()).build();
            rentalRepository.save(rental);

            Review review = Review.builder().ad(tux).user(user3).description("veldig bra anbefaler dette produktet!").rating(9).build();
            reviewRepository.save(review);
            review = Review.builder().ad(borremaskin).user(user2).description("Elendig produkt").rating(6).build();
            reviewRepository.save(review);
            review = Review.builder().ad(kjokkenmaskin).user(user3).description("ten out of ten would buy again").rating(6).build();
            reviewRepository.save(review);
            review = Review.builder().ad(pizzaspade).user(user4).description("two out of ten would never buy again").rating(1).build();
            reviewRepository.save(review);
            review = Review.builder().ad(matte).user(user2).description("Elendig produkt").rating(6).build();
            reviewRepository.save(review);
            review = Review.builder().ad(klovn).user(user3).description("ten out of ten would buy again").rating(6).build();
            reviewRepository.save(review);
            review = Review.builder().ad(newHammer).user(user4).description("two out of ten would never buy again").rating(1).build();
            reviewRepository.save(review);
            review = Review.builder().ad(newHammer).user(user2).description("Elendig produkt").rating(6).build();
            reviewRepository.save(review);
            review = Review.builder().ad(pc).user(user2).description("ten out of ten would buy again").rating(6).build();
            reviewRepository.save(review);
            review = Review.builder().ad(charger).user(user4).description("two out of ten would never buy again").rating(1).build();
            reviewRepository.save(review);

            // Add dates to the ads // todo might not work due to id
            List<Ad> ads =  adRepository.findAll();
            for(Ad ad : ads) {
                ad.setDates(calendarService.addFutureDates(ad.getId()));
            }

            for(Ad a:adRepository.findAll()){
                Set<Ad> adsy = new HashSet<>();
                adsy.add(a);
                a.getCategory().setAds(adsy);;
                categoryRepository.save(a.getCategory());
            }


            File pb = new File("src/main/resources/static/images/anders.jpg");
            savepb(pb, user1);
            pb = new File("src/main/resources/static/images/hasan.jpg");
            savepb(pb,user2);
            pb = new File("src/main/resources/static/images/maiken.jpg");
            savepb(pb, user4);
            pb = new File("src/main/resources/static/images/thadsha.jpeg");
            savepb(pb,user5);
            pb = new File("src/main/resources/static/images/karoline.jpg");
            savepb(pb, user6);
            pb = new File("src/main/resources/static/images/eirin.jpg");
            savepb(pb, user7);
            pb = new File("src/main/resources/static/images/johannes.jpg");
            savepb(pb, user8);
            pb = new File("src/main/resources/static/images/leo.jpg");
            savepb(pb, user9);
            pb = new File("src/main/resources/static/images/obama.jpg");
            savepb(pb, user3);

            File file = new File("src/main/resources/static/images/borrmaskin.jpg");
            fileContent(borremaskin, file);

            file = new File("src/main/resources/static/images/bil.jpg");
            fileContent(bil,file);
            file = new File("src/main/resources/static/images/bil1.jpg");
            fileContent(bil,file);

            file = new File("src/main/resources/static/images/bot.jpg");
            fileContent(bot, file);
            file = new File("src/main/resources/static/images/bot1.jpg");
            fileContent(bot, file);

            file = new File("src/main/resources/static/images/sykkel.jpg");
            fileContent(sykkel, file);
            file = new File("src/main/resources/static/images/sykkel1.jpg");
            fileContent(sykkel, file);

            file = new File("src/main/resources/static/images/sparkesykke.jpg");
            fileContent(sparkesykkel, file);

            file = new File("src/main/resources/static/images/moped.jpg");
            fileContent(moped, file);
            file = new File("src/main/resources/static/images/moped1.jpg");
            fileContent(moped, file);

            file = new File("src/main/resources/static/images/tilhenger.jpg");
            fileContent(tilhenger, file);

            file = new File("src/main/resources/static/images/gravemaskin.jpg");
            fileContent(gravemaskin, file);

            file = new File("src/main/resources/static/images/elsykkel.jpg");
            fileContent(elsykker, file);

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

            file = new File("src/main/resources/static/images/random/bekir-donmez-eofm5R5f9Kw-unsplash.jpg");
            fileContent(sovepose, file);

            file = new File("src/main/resources/static/images/random/david-kovalenko-G85VuTpw6jg-unsplash.jpg");
            fileContent(tux, file);

            file = new File("src/main/resources/static/images/random/diego-ph-fIq0tET6llw-unsplash.jpg");
            fileContent(motherBoard, file);

            file = new File("src/main/resources/static/images/random/ian-dooley-hpTH5b6mo2s-unsplash.jpg");
            fileContent(sovepose, file);

            file = new File("src/main/resources/static/images/random/jess-bailey-l3N9Q27zULw-unsplash.jpg");
            fileContent(pc, file);

            file = new File("src/main/resources/static/images/brod.jpg");
            fileContent(kjokkenmaskin, file);



            Group group1 = Group.builder().name("gruppechat1").build();
            Group group2 = Group.builder().name("gruppechat2").build();
            Group group3 = Group.builder().name("gruppechat3").build();

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

            adRepository.save(borremaskin);

            Message message1 = Message.builder().content("Hei!").group(group1).user(user1).timestamp(Timestamp.from(Instant.now())).build();
            Message message2 = Message.builder().content("Halo").group(group1).user(user2).timestamp(Timestamp.from(Instant.now())).build();
            Message message3 = Message.builder().content("Så fint vær idag.").group(group1).user(user2).timestamp(Timestamp.from(Instant.now())).build();
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
/*
            Set<CalendarDate> dates = borremaskin.getDates();
            for (CalendarDate d : dates){
                d.setAvailable(false);
            }
            borremaskin.setDates(dates);

            adRepository.save(borremaskin);

 */
        }

    private void fileContent(Ad ad, File file) throws IOException {
        byte[] fileContent;
        Picture picture;
        fileContent = Files.readAllBytes(file.toPath());
        picture = Picture.builder()
                .filename(file.getName())
                .data(fileContent)
                .type(Files.probeContentType(file.toPath()))
                .build();
        ad.setPictures(new HashSet<>());
        ad.getPictures().add(picture);
        picture.setAd(ad);
        adRepository.save(ad);
        pictureRepository.save(picture);
    }

    public void savepb(File pb, User user) throws IOException {
        byte[] fileContent =  Files.readAllBytes(pb.toPath());
        Picture picture1 = Picture.builder().filename(pb.getName())
                .data(fileContent)
                .type(Files.probeContentType(pb.toPath()))
                .build();
        user.setPicture(picture1);
        picture1.setUser(user);
        userRepository.save(user);
        pictureRepository.save(picture1);
    }
}
