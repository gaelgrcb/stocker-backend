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

    // Importante: Debe devolver List<Inventory>
    List<Inventory> findByUser(User user);

    Optional<Inventory> findByProduct(Product product);

    @Query("SELECT i FROM Inventory i WHERE i.user = :user AND i.available_quantity <= i.minimum_alert")
    List<Inventory> findLowStockByUser(@Param("user") User user);
}
