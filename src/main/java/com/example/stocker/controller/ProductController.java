package com.example.stocker.controller;

import com.example.stocker.model.User;
import com.example.stocker.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<List<ProductService.ProductResponseDTO>> getAll(@AuthenticationPrincipal User user) {
        var products = productService.getProducts(user).stream()
                .map(p -> new ProductService.ProductResponseDTO(
                        p.getId(),
                        p.getName(),
                        p.getModel(),
                        p.getFlavor(),
                        p.getCost(),
                        p.getPrice()))
                .toList();
        return ResponseEntity.ok(products);
    }

    @PostMapping("/create")
    public ResponseEntity<ProductService.ProductResponseDTO> create(
            @RequestBody ProductService.ProductRequestDTO dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(dto, user));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(
            @RequestBody ProductService.ProductRequestDTO dto,
            @AuthenticationPrincipal User user) {
        productService.deleteProductByAttributes(dto, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/edit")
    public ResponseEntity<ProductService.ProductResponseDTO> edit(
            @RequestBody ProductService.ProductRequestDTO dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(productService.editProductByAttributes(dto, user));
    }
}
