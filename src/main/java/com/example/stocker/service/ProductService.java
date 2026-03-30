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

    public List<ProductResponseDTO> getAllProducts(User user) {
        return productRepository.findByUser(user).stream()
                .map(p -> new ProductResponseDTO(
                        p.getId(),
                        p.getName(),
                        p.getModel(),
                        p.getFlavor(),
                        p.getCost(),
                        p.getPrice(),
                        // Si el inventario es null, ponemos valores por defecto
                        p.getInventory() != null ? p.getInventory().getAvailable_quantity() : 0,
                        p.getInventory() != null ? p.getInventory().getMinimum_alert() : 5
                ))
                .toList();
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productData, User user) {
        // 1. Validar duplicados
        boolean exist = productRepository.existsByNameAndModelAndFlavorAndUser(
                productData.name(), productData.model(), productData.flavor(), user
        );

        if (exist) {
            throw new RuntimeException("El producto ya existe para este usuario.");
        }

        // 2. Mapear y guardar Producto
        Product product = new Product();
        product.setName(productData.name());
        product.setModel(productData.model());
        product.setFlavor(productData.flavor());
        product.setCost(productData.cost());
        product.setPrice(productData.price());
        product.setUser(user);

        Product createdProduct = productRepository.save(product);

        // 3. Crear Inventario asociado con alerta personalizada
        Inventory inventory = new Inventory();
        inventory.setProduct(createdProduct);
        inventory.setUser(user);
        inventory.setAvailable_quantity(productData.stock() != null ? productData.stock() : 0);

        // El usuario decide la alerta mínima, si no, por defecto es 5
        inventory.setMinimum_alert(productData.minimumAlert() != null ? productData.minimumAlert() : 5);

        inventoryRepository.save(inventory);

        return new ProductResponseDTO(
                createdProduct.getId(),
                createdProduct.getName(),
                createdProduct.getModel(),
                createdProduct.getFlavor(),
                createdProduct.getCost(),
                createdProduct.getPrice(),
                inventory.getAvailable_quantity(),
                inventory.getMinimum_alert()
        );
    }

    @Transactional
    public ProductResponseDTO editProductById(Long id, ProductRequestDTO newData, User user) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el producto para editar"));

        if (!product.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permisos para editar este producto");
        }

        product.setName(newData.name());
        product.setModel(newData.model());
        product.setFlavor(newData.flavor());
        product.setCost(newData.cost());
        product.setPrice(newData.price());

        if (product.getInventory() != null) {
            if (newData.stock() != null) product.getInventory().setAvailable_quantity(newData.stock());
            if (newData.minimumAlert() != null) product.getInventory().setMinimum_alert(newData.minimumAlert());
        }

        productRepository.save(product);

        return new ProductResponseDTO(
                product.getId(), product.getName(), product.getModel(),
                product.getFlavor(), product.getCost(), product.getPrice(),
                product.getInventory() != null ? product.getInventory().getAvailable_quantity() : 0,
                product.getInventory() != null ? product.getInventory().getMinimum_alert() : 5
        );
    }

    @Transactional
    public void deleteProductByAttributes(ProductRequestDTO productData, User user) {
        Product product = productRepository.findByNameAndModelAndFlavorAndUser(
                productData.name(), productData.model(), productData.flavor(), user
        ).orElseThrow(() -> new RuntimeException("No se encontró el producto para eliminar"));

        productRepository.delete(product);
    }

    // --- MÉTODOS DE MÉTRICAS ---

    public DashboardMetricsDTO getDashboardMetrics(User user) {
        List<Inventory> inventories = inventoryRepository.findByUser(user);

        BigDecimal totalEarnings = BigDecimal.ZERO;

        long lowStockCount = inventories.stream()
                .filter(inv -> inv.getAvailable_quantity() <= inv.getMinimum_alert())
                .count();

        return new DashboardMetricsDTO(
                totalEarnings,
                (int) lowStockCount,
                0
        );
    }

    // --- RECORDS (DTOs) ---

    public record ProductRequestDTO(
            String name,
            String model,
            String flavor,
            BigDecimal cost,
            BigDecimal price,
            Integer stock,
            Integer minimumAlert
    ) {}

    public record ProductResponseDTO(
            Long id,
            String name,
            String model,
            String flavor,
            BigDecimal cost,
            BigDecimal price,
            Integer stock,
            Integer minimumAlert
    ) {}

    public record DashboardMetricsDTO(
            BigDecimal earnings,
            Integer lowStock,
            Integer totalSales
    ) {}
}