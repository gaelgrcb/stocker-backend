package com.example.stocker.service;

import com.example.stocker.model.Inventory;
import com.example.stocker.model.Product;
import com.example.stocker.model.User;
import com.example.stocker.repository.InventoryRepository;
import com.example.stocker.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public Inventory addStock(Product product, int quantity, User user) {
        if (quantity <= 0) {
            throw new RuntimeException("La cantidad de productos debe ser mayor que cero");
        }

        Optional<Inventory> inventoryOpt = inventoryRepository.findByProduct(product);

        if (inventoryOpt.isEmpty()) {
            throw new RuntimeException("No se encontró registro de inventario para este producto.");
        }

        Inventory inventory = inventoryOpt.get();

        if (!inventory.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para modificar este inventario.");
        }

        int newQuantity = inventory.getAvailable_quantity() + quantity;
        inventory.setAvailable_quantity(newQuantity);

        return inventoryRepository.save(inventory);
    }

    public Optional<Inventory> getStockByUser(User user) {
        return inventoryRepository.findAllByUser(user);
    }
}
