package com.example.stocker.repository;

import com.example.stocker.model.Product;
import com.example.stocker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByUser(User user);

    List<Product> findByNameAndUser(String name, User user);

    Boolean existsByNameAndModelAndFlavorAndUser(String name, String model, String flavor, User user);
}
