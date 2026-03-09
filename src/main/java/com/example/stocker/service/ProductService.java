package com.example.stocker.service;

import com.example.stocker.model.Inventory;
import com.example.stocker.model.Product;
import com.example.stocker.model.User;
import com.example.stocker.repository.InventoryRepository;
import com.example.stocker.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService{

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public List<Product> getProducts(User user){
        return productRepository.findByUser(user);
    }

    @Transactional
    public productDTO createProduct(productDTO productData, User user){
        boolean exist = productRepository.existsByNameAndModelAndFlavorAndUser(
                productData.name,
                productData.model,
                productData.flavor,
                user
        );

        if (exist){
            throw new RuntimeException("El producto " + productData.name + " ya existe para este usuario.");
        }

        Product product = new Product();
        product.setName(productData.name);
        product.setModel(productData.model);
        product.setFlavor(productData.flavor);
        product.setCost(productData.cost);
        product.setPrice(productData.price);
        product.setUser(user);

        Product createdProduct = productRepository.save(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(createdProduct);
        inventory.setUser(user);
        inventory.setAvailable_quantity(0);
        inventory.setMinimum_alert(5);

        inventoryRepository.save(inventory);

        return productData;
    }

    public record productDTO(
        String name,
        String model,
        String flavor,
        BigDecimal cost,
        BigDecimal price
    ) {}
}
