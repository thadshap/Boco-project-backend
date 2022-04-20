package com.example.idatt2106_2022_05_backend.model;

import lombok.AllArgsConstructor;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="GEO_ID", nullable=false)
    private long geo_id;

    @Column(name="LAT")
    private double lat;

    @Column(name="LNG")
    private  double lng;

    //TODO: add relation to AD

}
