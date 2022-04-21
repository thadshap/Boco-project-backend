package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    ResetPasswordToken getByToken(String token);

    ResetPasswordToken findByToken(String token);

}
