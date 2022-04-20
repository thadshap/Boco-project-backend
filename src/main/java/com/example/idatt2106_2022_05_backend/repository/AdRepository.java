package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface AdRepository extends JpaRepository<Ad, Long> {

    // Get all available ads
    @Query("SELECT a FROM Ad a WHERE a.rentedOut= false")
    Set<Ad> getAllAvailableAds();


    // Get all available ads by user id
    @Query("SELECT a FROM Ad a WHERE a.user.id= :id")
    Set<Ad> getAvailableAdsByUserId(@Param("id") long id);

}
