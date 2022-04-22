package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.RentalDto;
import com.example.idatt2106_2022_05_backend.service.rental.RentalService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("rental")
@Api(tags = "Controller class to handle rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @PostMapping("/create")
    @ApiOperation(value = "Endpoint to create a rental", response = Response.class)
    public Response createRental(@RequestBody RentalDto rentalDto){
        return rentalService.createRental(rentalDto);
    }

    @DeleteMapping("/delete/{rentalId}")
    @ApiOperation(value = "Endpoint to delete a rental", response = Response.class)
    public Response deleteRental( @PathVariable Long rentalId){
        return rentalService.deleteRental(rentalId);
    }

    @PostMapping("/update")
    @ApiOperation(value = "Endpoint to update a rental", response = Response.class)
    public Response updateRental(@RequestBody RentalDto rentalDto){
        return rentalService.updateRental(rentalDto);//TODO real objects to return
    }

    @GetMapping("/")
    @ApiOperation(value = "Endpoint to get a rental", response = Response.class)
    public Response getRental(@RequestBody RentalDto rentalDto){
        return rentalService.getRental(rentalDto);
    }

    @GetMapping("/{userId}")
    @ApiOperation(value = "Endpoint to get a rental by user id", response = Response.class)
    public Response getRentalByUserId( @PathVariable Long userId){
        return rentalService.getRentalByUserId(userId);
    }

    @GetMapping("s/{userId}")
    @ApiOperation(value = "Endpoint to get list of rentals by user id", response = Response.class)
    public Response getRentalsByUserId( @PathVariable Long userId){
        return rentalService.getRentalsByUserId(userId);
    }
}
