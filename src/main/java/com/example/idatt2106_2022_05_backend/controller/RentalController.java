package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.rental.RentalDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalReviewDto;
import com.example.idatt2106_2022_05_backend.dto.rental.RentalUpdateDto;
import com.example.idatt2106_2022_05_backend.security.SecurityService;
import com.example.idatt2106_2022_05_backend.service.rental.RentalService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@Slf4j
@RestController()
@RequestMapping("rental")
@Api(tags = "Controller class to handle rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private SecurityService securityService;

    @PostMapping("/create")
    @ApiOperation(value = "Endpoint to create a rental", response = Response.class)
    public Response createRental(@RequestBody RentalDto rentalDto) {
        log.debug("[X] Call to create a rental of ad with id = {}", rentalDto.getAdId());
        if (!securityService.isUserByEmail(rentalDto.getBorrower()) && !securityService.isVerifiedUser(0L)) {
            return new Response("Du kan ikke leie dette produktet.", HttpStatus.BAD_REQUEST);
        }
        return rentalService.createRental(rentalDto);
    }

    @PutMapping("/activate/{rentalId}")
    @ApiOperation(value = "Endpoint to create a rental", response = Response.class)
    public Response activateRental(@PathVariable Long rentalId) throws MessagingException {
        log.debug("[X] Call to activate a rental of ad with id = {}", rentalId);
        if (!securityService.isRentalOwner(rentalId)) {
            return new Response("Du har ikke tilgang på forespørselen.", HttpStatus.BAD_REQUEST);
        }
        return rentalService.activateRental(rentalId);
    }

    @DeleteMapping("/delete/{rentalId}")
    @ApiOperation(value = "Endpoint to delete a rental", response = Response.class)
    public Response deleteRental(@PathVariable Long rentalId, @RequestBody RentalReviewDto rentalDto) {
        log.debug("[X] Call to delete a rental with id = {}", rentalId);
        if (!securityService.isRentalBorrower(rentalId)) {
            return new Response("Du har ikke tilgang på forespørselen.", HttpStatus.BAD_REQUEST);
        }
        return rentalService.completeRental(rentalId, rentalDto);
    }

    @PutMapping("/update/{rentalId}")
    @ApiOperation(value = "Endpoint to update a rental", response = Response.class)
    public Response updateRental(@RequestBody RentalUpdateDto rentalDto, @PathVariable Long rentalId) {
        log.debug("[X] Call to update rental with id = {}", rentalId);
        if (!securityService.isRentalOwner(rentalId)) {
            return new Response("Du har ikke tilgang på forespørselen.", HttpStatus.BAD_REQUEST);
        }
        return rentalService.updateRental(rentalDto, rentalId);// TODO real objects to return
    }

    @GetMapping("/{rentalId}")
    @ApiOperation(value = "Endpoint to get a rental", response = Response.class)
    public Response getRental(@PathVariable Long rentalId) {
        log.debug("[X] Call to get rental with id = {}", rentalId);
        if (!securityService.isRentalOwner(rentalId) && !securityService.isRentalBorrower(rentalId)) {
            return new Response("Du har ikke tilgang på forespørselen.", HttpStatus.BAD_REQUEST);
        }
        return rentalService.getRental(rentalId);
    }

    @GetMapping("/s/{userId}")
    @ApiOperation(value = "Endpoint to get list of rentals by user id", response = Response.class)
    public Response getRentalsByUserId(@PathVariable Long userId) {
        log.debug("[X] Call to get all rentals of user with id = {}", userId);
        if (!securityService.isUser(userId)) {
            return new Response("Du har ikke tilgang på forespørselen.", HttpStatus.BAD_REQUEST);
        }
        return rentalService.getRentalsByUserId(userId);
    }

    @GetMapping("/s/picture/{rentalId}")
    @ApiOperation(value = "Endpoint to get picture of rentals by user id", response = Response.class)
    public Response getRentalPictureById(@PathVariable Long rentalId) {
        log.debug("[X] Call to get all rentals of user with id = {}", rentalId);
        if (!securityService.isRentalOwner(rentalId) || !securityService.isRentalOwner(rentalId)) {
            return new Response("Du har ikke tilgang på forespørselen.", HttpStatus.BAD_REQUEST);
        }
        return rentalService.getRentalPictureById(rentalId);
    }
}
