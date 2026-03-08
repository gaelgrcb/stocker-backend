package com.example.stocker.repository;

import com.example.stocker.model.Product;
import com.example.stocker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByUser(User user);

    List<Product> findByNameAndUser(String name, User user);

    Boolean existsByNameAndModelAndFlavorAndUser(String name, String model, String flavor, User user);

    @Query("SELECT i.price FROM Product i WHERE i.user = :user AND i.name = :name")
    BigDecimal findPriceByUserAndName(@Param("user") User user, @Param("name") String name);
}
