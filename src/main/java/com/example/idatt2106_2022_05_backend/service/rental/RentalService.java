package com.example.idatt2106_2022_05_backend.service.rental;

import com.example.idatt2106_2022_05_backend.dto.PictureReturnDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalReviewDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalUpdateDto;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import java.io.IOException;

@Service
public interface RentalService {
    Response createRental(RentalDto rentalDto) throws MessagingException, IOException;

//    ModelAndView approveRental(Long rentalId, String token);

    Response activateRental(Long rentalId) throws MessagingException, IOException;

    Response declineRental(Long rentalId) throws MessagingException, IOException;

    Response completeRental(Long rentalId, RentalReviewDto rentalDto);

    Response updateRental(RentalUpdateDto rentalDto, Long rentalId);

    Response getRental(Long rentalId);

    Response getRentalsByUserId(Long userId);

    Response getRentalPictureById(Long rentalId);
}
