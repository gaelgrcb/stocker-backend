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
    private final ProductRepository productRepository;

    @Transactional
    public Sale createSale(List<Product> products, List<Integer> quantities, User user) {
        Sale newSale = new Sale();
        newSale.setUser(user);
        newSale.setDate(LocalDate.now());
        newSale = saleRepository.save(newSale);
        BigDecimal totalFinal = BigDecimal.ZERO;

        for (int i = 0; i < products.size(); i++){
            Product product = products.get(i);
            int quantityToSell = quantities.get(i);

            Integer stock = inventoryRepository.getStockSpecific(user, product);
            int currentStock = (stock != null) ? stock : 0;
            if (currentStock <= 0){
                throw new RuntimeException("No hay suficientes productos para realizar esta venta");
            }

            int newQuantity = currentStock - quantityToSell;
            Optional<Inventory> inventoryOp = inventoryRepository.findByProduct(product);
            Inventory inventory = inventoryOp.get();

            inventory.setAvailable_quantity(newQuantity);
            inventoryRepository.save(inventory);

            BigDecimal priceOfStock = productRepository.findPriceByUserAndName(user, product.getName());
            BigDecimal subtotal = priceOfStock.multiply(BigDecimal.valueOf(quantityToSell));

            SaleDetail detail = new SaleDetail();
            detail.setSale(newSale);
            detail.setProduct(product);
            detail.setQuantity(quantityToSell);
            detail.setPrice_applied(subtotal);
            saleDetailRepository.save(detail);

            totalFinal = totalFinal.add(subtotal);
        }
        newSale.setTotal_earnings(totalFinal);
        return saleRepository.save(newSale);
    }
}