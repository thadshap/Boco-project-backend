package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // get all ads for user (by user id)
    @Query("SELECT u.ads FROM User u WHERE u.id= :id")
    Set<Ad> getAdsByUserId(@Param("id") long id);

    User findByEmail(String email);

    Optional<User> findUserByEmail(String email);

    Optional<User> findById(long user_id);

    boolean existsByEmail(String email);

    User getByEmail(String owner);
}
