package com.example.stocker.controller;

import com.example.stocker.model.User;
import com.example.stocker.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @PostMapping("/create")
    public ProductService.productDTO create(@RequestBody ProductService.productDTO dto, @AuthenticationPrincipal User user){
        return productService.createProduct(dto, user);
    }
    @PostMapping("/delete")
    public ProductService.productDTO delete(@RequestBody ProductService.productDTO dto, @AuthenticationPrincipal User user){
        return productService.deleteProduct(dto, user);
    }
    @PostMapping("/edit")
    public ProductService.productDTO edit(@RequestBody ProductService.productDTO dto, @AuthenticationPrincipal User user){
        return productService.editProduct(dto, user);
    }
}
