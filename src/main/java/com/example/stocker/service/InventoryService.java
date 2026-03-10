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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public InventoryResponseDTO addStock(UpdateStockRequestDTO request, User user) {
        if (request.quantity() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor que cero");
        }

        Product product = productRepository.findByNameAndModelAndFlavorAndUser(
                request.productName(), request.productModel(), request.productFlavor(), user
        ).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("No hay registro de inventario"));

        inventory.setAvailable_quantity(inventory.getAvailable_quantity() + request.quantity());
        Inventory updated = inventoryRepository.save(inventory);

        return mapToResponseDTO(updated);
    }

    public List<InventoryResponseDTO> getInventoryByUser(User user) {
        Optional<Inventory> inventories = inventoryRepository.findAllByUser(user);
        return inventories.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private InventoryResponseDTO mapToResponseDTO(Inventory inventory) {
        return new InventoryResponseDTO(
                inventory.getId(),
                inventory.getProduct().getName(),
                inventory.getProduct().getModel(),
                inventory.getProduct().getFlavor(),
                inventory.getAvailable_quantity(),
                inventory.getMinimum_alert()
        );
    }

    public record InventoryResponseDTO(
            Long id,
            String productName,
            String productModel,
            String productFlavor,
            int availableQuantity,
            int minimumAlert
    ) {
    }

    public record UpdateStockRequestDTO(
            String productName,
            String productModel,
            String productFlavor,
            int quantity
    ) {
    }
}