package com.example.stocker.service;

import com.example.stocker.model.Inventory;
import com.example.stocker.model.Product;
import com.example.stocker.model.User;
import com.example.stocker.repository.InventoryRepository;
import com.example.stocker.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public Product createProduct(Product product, User user){
        boolean exist = productRepository.existsByNameAndModelAndFlavorAndUser(
                product.getName(),
                product.getModel(),
                product.getFlavor(),
                user
        );

        if (exist){
            throw new RuntimeException("El producto " + product.getName() + " ya existe para este usuario.");
        }

        product.setUser(user);
        Product createdProduct = productRepository.save(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(createdProduct);
        inventory.setUser(user);
        inventory.setAvailable_quantity(0);
        inventory.setMinimum_alert(5);

        inventoryRepository.save(inventory);

        return createdProduct;
    }
}
