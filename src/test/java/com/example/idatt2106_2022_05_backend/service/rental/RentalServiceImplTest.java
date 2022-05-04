package com.example.idatt2106_2022_05_backend.service.rental;

import com.example.idatt2106_2022_05_backend.dto.rental.RentalDto;
import com.example.idatt2106_2022_05_backend.enums.AdType;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Rental;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CalendarDateRepository;
import com.example.idatt2106_2022_05_backend.repository.RentalRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.service.email.EmailService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.linesOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {



    @Mock
    UserRepository userRepository;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Mock
    private RentalRepository rentalRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private CalendarDateRepository dayDateRepository;

    @Autowired
    private EmailService emailService;

    private ModelMapper modelMapper = new ModelMapper();

    User user;
    User user2;

    Ad ad;

    Rental rental;

    Response response;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("test")
                .lastName("Testesen")
                .email("test@test.no")
                .password("passor1")
                .build();

        user = User.builder()
                .id(2L)
                .firstName("test")
                .lastName("Testesen")
                .email("test@test.no")
                .password("passor12")
                .build();

        ad = Ad.builder().
                title("New pants").
                description("Renting out a pair of pants in size 36").
                rental(true).
                durationType(AdType.MONTH).
                duration(2).
                price(100).
                streetAddress("Project Road 4").
                postalCode(7200).
                user(user).
                build();

        rental = Rental.builder()
                .id(1L)
                .dateOfRental(LocalDate.now())
                .rentFrom(LocalDate.now().plusDays(1))
                .rentTo(LocalDate.now().plusDays(2))
                .deadline(LocalDate.now())
                .active(false)
//                .owner()
//                .borrower()
//                .ad()
                .build();

        assert rental != null;
        lenient().when(rentalRepository.findById(rental.getId())).thenReturn(Optional.ofNullable(rental));
    }

    @Test
    void createRental() {
        RentalDto rentalDto = RentalDto.builder()
                .dateOfRental(rental.getDateOfRental())
                .rentFrom(rental.getRentFrom())
                .rentTo(rental.getRentTo())
                .deadline(rental.getDeadline())
                .owner(user.getEmail())
                .borrower(user2.getEmail())
                .adId(ad.getId())
                .build();

        Mockito.when(adRepository.getById(rentalDto.getAdId())).thenReturn(rental.getAd());
        Mockito.when(userRepository.getByEmail(rentalDto.getOwner())).thenReturn(rental.getOwner());
        Mockito.when(userRepository.getByEmail(rentalDto.getBorrower())).thenReturn(rental.getBorrower());
        response = new Response("Rental object is now created", HttpStatus.OK);
        Response realResponse = rentalService.createRental(rentalDto);
        assertEquals(response.getBody(),(realResponse.getBody()));
    }



    @Test
    void activateRental() {
    }

    @Test
    void deleteRental() {
    }

    @Test
    void updateRental() {
    }

    @Test
    void getRental() {
    }

    @Test
    void getRentalsByUserId() {
    }
}
