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
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public List<Product> getProducts(User user) {
        return productRepository.findByUser(user);
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productData, User user) {
        boolean exist = productRepository.existsByNameAndModelAndFlavorAndUser(
                productData.name(), productData.model(), productData.flavor(), user
        );

        if (exist) {
            throw new RuntimeException("El producto ya existe para este usuario.");
        }

        Product product = new Product();
        product.setName(productData.name());
        product.setModel(productData.model());
        product.setFlavor(productData.flavor());
        product.setCost(productData.cost());
        product.setPrice(productData.price());
        product.setUser(user);

        Product createdProduct = productRepository.save(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(createdProduct);
        inventory.setUser(user);
        inventory.setAvailable_quantity(0);
        inventory.setMinimum_alert(5);

        inventoryRepository.save(inventory);

        return new ProductResponseDTO(
                createdProduct.getId(),
                createdProduct.getName(),
                createdProduct.getModel(),
                createdProduct.getFlavor(),
                createdProduct.getCost(),
                createdProduct.getPrice()
        );
    }

    @Transactional
    public ProductResponseDTO deleteProductByAttributes(ProductRequestDTO productData, User user) {
        Product product = productRepository.findByNameAndModelAndFlavorAndUser(
                productData.name(),
                productData.model(),
                productData.flavor(),
                user
        ).orElseThrow(() -> new RuntimeException("No se encontró el producto exacto para eliminar"));

        productRepository.delete(product);

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getModel(),
                product.getFlavor(),
                product.getCost(),
                product.getPrice()
        );
    }

    @Transactional
    public ProductResponseDTO editProductByAttributes(ProductRequestDTO newData, User user) {
        Product product = productRepository.findByNameAndModelAndFlavorAndUser(
                newData.name(),
                newData.model(),
                newData.flavor(),
                user
        ).orElseThrow(() -> new RuntimeException("No se encontró el producto para editar"));

        product.setCost(newData.cost());
        product.setPrice(newData.price());

        productRepository.save(product);

        return new ProductResponseDTO(
                product.getId(), product.getName(), product.getModel(),
                product.getFlavor(), product.getCost(), product.getPrice()
        );
    }

    public record ProductRequestDTO(
            String name,
            String model,
            String flavor,
            BigDecimal cost,
            BigDecimal price
    ) {
    }

    public record ProductResponseDTO(
            Long id,
            String name,
            String model,
            String flavor,
            BigDecimal cost,
            BigDecimal price
    ) {
    }
}