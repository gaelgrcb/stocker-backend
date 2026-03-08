package com.example.stocker.repository;

import com.example.stocker.model.Sale;
import com.example.stocker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT s FROM Sale s WHERE s.user = :user ORDER BY s.date ASC")
    List<Sale> findAllByUserOrderByDateAsc(@Param("user") User user);

    List<Sale> findByUserAndDateBetween(User user, LocalDateTime start, LocalDateTime end);
}
