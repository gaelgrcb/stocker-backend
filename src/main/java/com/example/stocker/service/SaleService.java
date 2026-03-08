package com.example.stocker.service;

import com.example.stocker.model.*;
import com.example.stocker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public Sale createSale(List<Product> products, List<Integer> quantities, User user) {
        Sale newSale = new Sale();
        newSale.setUser(user);
        newSale.setDate(LocalDate.now());
        newSale = saleRepository.save(newSale);
        BigDecimal totalFinal = BigDecimal.ZERO;

        for (int i = 0; i < products.size(); i++){
            Product product = products.get(i);
            int qty = quantities.get(i);

            Inventory inventory = inventoryRepository.findByProduct(product)
                    .orElseThrow(() -> new RuntimeException("No existe inventario para: " + product.getName()));

            if (inventory.getAvailable_quantity() < qty) {
                throw new RuntimeException("Stock insuficiente para " + product.getName());
            }

            inventory.setAvailable_quantity(inventory.getAvailable_quantity() - qty);
            inventoryRepository.save(inventory);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(qty));

            SaleDetail detail = new SaleDetail();
            detail.setSale(newSale);
            detail.setProduct(product);
            detail.setQuantity(qty);
            detail.setPrice_applied(subtotal);
            saleDetailRepository.save(detail);

            totalFinal = totalFinal.add(subtotal);
        }
        newSale.setTotal_earnings(totalFinal);
        return saleRepository.save(newSale);
    }
}