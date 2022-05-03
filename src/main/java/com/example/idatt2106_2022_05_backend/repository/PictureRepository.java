package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Picture;
import com.example.idatt2106_2022_05_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {
    Optional<Picture> findByFilename(String name);

    Optional<Picture> findByAdAndId(Ad ad, long picture_id);

    List<Picture> findByAd(Ad ad);

    Picture getByUser(User user);

    List<Picture> findByUser(User user);

    @Transactional
    void deleteByUser(User user);

    Picture getByAd(Long id);
}
