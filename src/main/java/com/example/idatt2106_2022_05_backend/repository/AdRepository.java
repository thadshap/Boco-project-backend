package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.CalendarDate;
import com.example.idatt2106_2022_05_backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

public interface AdRepository extends JpaRepository<Ad, Long> {

    // Get all available ads
    @Query("SELECT a FROM Ad a WHERE a.rentedOut= false")
    Set<Ad> getAllAvailableAds();

    // Get all available ads by user id
    @Query("SELECT a FROM Ad a WHERE a.user.id= :id")
    Set<Ad> getAvailableAdsByUserId(@Param("id") long id);

    // Get all reviews for an add where ad-owner = user id
    @Query("SELECT a.reviews FROM Ad a WHERE a.user.id = :id")
    Set<Review> getReviewsByUserId(@Param("id") long id);

    // Get all dates for ad
    @Query("SELECT a.dates FROM Ad a WHERE a.adId = :id")
    Set<CalendarDate> getDatesForAd(@Param("id") long id);

    // Get all ads with a specific postal code
    Set<Ad> findByPostalCode(int postalCode);

    // Get all ads with items that are either being rented or given away
    Set<Ad> findByRental(boolean rental);

}
