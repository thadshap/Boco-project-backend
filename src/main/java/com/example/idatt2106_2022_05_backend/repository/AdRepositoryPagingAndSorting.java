package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.Ad;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface AdRepositoryPagingAndSorting extends PagingAndSortingRepository<Ad, Long> {
    List<Ad> getAllByPrice(double price, Pageable pageable);
}
