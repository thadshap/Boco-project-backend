package com.example.idatt2106_2022_05_backend.service.rental;

import com.example.idatt2106_2022_05_backend.dto.RentalDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Rental;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.RentalRepository;
import com.example.idatt2106_2022_05_backend.repository.UserRepository;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalServiceImpl implements RentalService{

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    @Override
    public Response createRental(RentalDto rentalDto) {
        Ad ad = adRepository.getById(rentalDto.getAd());
        List<Rental> rentals = rentalRepository.findByAd(ad);
        return null;
    }

    @Override
    public Response deleteRental(Long rentalId) {

        return null;
    }

    @Override
    public Response updateRental(RentalDto rentalDto) {

        return null;
    }

    @Override
    public Response getRental(RentalDto rentalDto) {

        return null;
    }

    @Override
    public Response getRentalByUserId(Long userId) {

        return null;
    }

    @Override
    public Response getRentalsByUserId(Long userId) {

        return null;
    }
}
