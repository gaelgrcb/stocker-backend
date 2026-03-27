package com.example.stocker.repository;

import com.example.stocker.model.Sale;
import com.example.stocker.model.User;
import com.example.stocker.service.SaleService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT s FROM Sale s WHERE s.user = :user ORDER BY s.date ASC")
    List<Sale> findAllByUserOrderByDateAsc(@Param("user") User user);

    @Query("SELECT s FROM Sale s WHERE s.user = :user " +
            "AND (:startDate IS NULL OR s.date >= :startDate) " +
            "AND (:endDate IS NULL OR s.date <= :endDate)")
    List<Sale> findSalesByFilters(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
