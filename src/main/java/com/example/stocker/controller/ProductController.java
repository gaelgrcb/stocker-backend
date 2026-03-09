package com.example.stocker.controller;

import com.example.stocker.model.User;
import com.example.stocker.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @PostMapping("/create")
    public ProductService.productDTO create(@RequestBody ProductService.productDTO dto, User user){ return productService.createProduct(dto, user); }
}
