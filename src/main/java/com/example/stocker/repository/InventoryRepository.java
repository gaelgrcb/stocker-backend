package com.example.stocker.repository;

import com.example.stocker.model.Inventory;
import com.example.stocker.model.Product;
import com.example.stocker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct(Product product);

    @Query("SELECT i FROM Inventory i WHERE i.user = :user AND i.available_quantity <= i.minimum_alert")
    List<Inventory> findLowStockByUser(@Param("user") User user);

    Optional<Inventory> findAllByUser(User user);

    @Query("SELECT i.available_quantity FROM Inventory i " +
            "WHERE i.user = :user AND i.product = :product")
    Integer getStockSpecific(@Param("user") User user, @Param("product") Product product);
}
