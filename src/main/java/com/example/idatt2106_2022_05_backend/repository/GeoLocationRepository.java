package com.example.idatt2106_2022_05_backend.repository;


import com.example.idatt2106_2022_05_backend.model.GeoLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoLocationRepository extends JpaRepository<GeoLocation, Long> {
}
