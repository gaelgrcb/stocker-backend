package com.example.stocker.controller;

import com.example.stocker.model.User;
import com.example.stocker.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/add")
    public ResponseEntity<InventoryService.InventoryResponseDTO> addStock(
            @RequestBody InventoryService.UpdateStockRequestDTO dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(inventoryService.addStock(dto, user));
    }

    @PutMapping("/update")
    public ResponseEntity<InventoryService.InventoryResponseDTO> updateStock(
            @RequestBody InventoryService.UpdateStockRequestDTO dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(inventoryService.addStock(dto, user));
    }
}
