package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.Rental;
import com.example.idatt2106_2022_05_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> getByOwner(User owner);

    List<Rental> getByBorrower(User owner);
}
