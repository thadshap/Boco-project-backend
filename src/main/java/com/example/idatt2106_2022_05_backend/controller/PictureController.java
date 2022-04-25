package com.example.idatt2106_2022_05_backend.controller;


import com.example.idatt2106_2022_05_backend.dto.UpdatePictureDto;
import com.example.idatt2106_2022_05_backend.service.Picture.PictureService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@Api(tags = "CRUD for picture")
public class PictureController {

    @Autowired
    PictureService pictureService;

    @GetMapping("/picture/{id}")
    @ApiOperation(value = "Endpoint to get an image based on id")
    public Response getImage(@PathVariable long id) throws IOException {
        return pictureService.getPicture(id);
    }

    @PostMapping("/picture")
    @ApiOperation(value = "endpoint to upload a new image")
    public Response upLoadPicture(@RequestBody UpdatePictureDto updatePictureDto) throws IOException {
        return pictureService.postNewPicture(updatePictureDto.getFile(), updatePictureDto.getAd_id());
    }

    @DeleteMapping("/picture/{filename}")
    @ApiOperation(value = "endpoint to delete picture")
    public Response deletePicture(@PathVariable String filename){
        return pictureService.deletePicture(filename);
    }
}
