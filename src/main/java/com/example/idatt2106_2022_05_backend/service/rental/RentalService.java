package com.example.idatt2106_2022_05_backend.service.rental;

import com.example.idatt2106_2022_05_backend.dto.RentalDto;
import com.example.idatt2106_2022_05_backend.util.Response;

public interface RentalService {
    Response createRental(RentalDto rentalDto);

    Response deleteRental(Long rentalId);

    Response updateRental(RentalDto rentalDto);

    Response getRental(RentalDto rentalDto);

    Response getRentalByUserId(Long userId);

    Response getRentalsByUserId(Long userId);
}
