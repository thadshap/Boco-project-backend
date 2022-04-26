package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Review;
import com.example.idatt2106_2022_05_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> getAllByUser(User user);
    List<Review> getAllByAd(Ad ad);
    Optional<Review> getByAdAndUser(Ad ad, User user);
    Set<Review> getAllByAd_Id(long adId);

}
