package com.example.stocker.controller;

import com.example.stocker.model.User;
import com.example.stocker.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @GetMapping("/all")
    public ResponseEntity<List<SaleService.SaleResponseDTO>> getAll(@AuthenticationPrincipal User user) {
        List<SaleService.SaleResponseDTO> sales = saleService.getSales(null, null, user);
        return ResponseEntity.ok(sales);
    }

    @PostMapping("/new")
    public ResponseEntity<SaleService.SaleResponseDTO> sale(
            @RequestBody SaleService.SaleRequestDTO dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.createSale(dto, user));
    }
}
