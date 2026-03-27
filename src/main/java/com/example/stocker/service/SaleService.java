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
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final SaleDetailRepository saleDetailRepository;

    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO request, User user) {
        Sale newSale = new Sale();
        newSale.setUser(user);
        newSale.setDate(LocalDate.now());
        newSale.setTotal_earnings(BigDecimal.ZERO);
        final Sale savedSale = saleRepository.save(newSale);

        BigDecimal totalFinal = BigDecimal.ZERO;

        for (SaleItemRequestDTO item : request.items()) {
            Product product = productRepository.findByNameAndModelAndFlavorAndUser(
                    item.name(), item.model(), item.flavor(), user
            ).orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.name()));

            Inventory inventory = inventoryRepository.findByProduct(product)
                    .orElseThrow(() -> new RuntimeException("Sin inventario para: " + product.getName()));

            if (inventory.getAvailable_quantity() < item.quantity()) {
                throw new RuntimeException("Stock insuficiente para: " + product.getName());
            }

            inventory.setAvailable_quantity(inventory.getAvailable_quantity() - item.quantity());
            inventoryRepository.save(inventory);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.quantity()));

            SaleDetail detail = new SaleDetail();
            detail.setSale(savedSale);
            detail.setProduct(product);
            detail.setQuantity(item.quantity());
            detail.setPrice_applied(product.getPrice());
            saleDetailRepository.save(detail);

            totalFinal = totalFinal.add(subtotal);
        }

        savedSale.setTotal_earnings(totalFinal);
        saleRepository.save(savedSale);

        return new SaleResponseDTO(
                savedSale.getId(),
                savedSale.getDate(),
                totalFinal,
                request.items().stream().map(i -> i.name() + " x" + i.quantity()).toList()
        );
    }

    @Transactional
    public List<SaleResponseDTO> getSales(LocalDate startDate, LocalDate endDate, User user) {

        List<Sale> sales = saleRepository.findSalesByFilters(user, startDate, endDate);

        return sales.stream()
                .map(sale -> new SaleResponseDTO(
                        sale.getId(),
                        sale.getDate(),
                        sale.getTotal_earnings(),
                        List.of()
                ))
                .toList();
    }

    public record SaleItemRequestDTO(
            String name,
            String model,
            String flavor,
            int quantity
    ) {
    }

    public record SaleRequestDTO(
            List<SaleItemRequestDTO> items
    ) {
    }

    public record SaleResponseDTO(
            Long saleId,
            LocalDate date,
            BigDecimal totalEarnings,
            List<String> productSummary
    ) {
    }
}