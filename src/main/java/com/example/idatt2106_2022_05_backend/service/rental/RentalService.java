package com.example.idatt2106_2022_05_backend.service.rental;

import com.example.idatt2106_2022_05_backend.dto.RentalDto;
import com.example.idatt2106_2022_05_backend.dto.RentalReviewDto;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.web.bind.annotation.PathVariable;

public interface RentalService {
    Response createRental(RentalDto rentalDto);

    Response deleteRental(Long rentalId, RentalReviewDto rentalDto);

    Response updateRental(RentalDto rentalDto, Long rentalId);

    Response getRental(Long rentalId);

    Response getRentalsByUserId(Long userId);
}
