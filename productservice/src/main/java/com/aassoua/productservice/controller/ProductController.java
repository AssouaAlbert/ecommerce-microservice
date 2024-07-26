package com.aassoua.productservice.controller;

import com.aassoua.productservice.dto.ProductRequest;
import com.aassoua.productservice.dto.ProductResponse;
import com.aassoua.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<ProductResponse>> createProduct(@RequestBody List<ProductRequest> productRequest) {
        List<ProductResponse> productResponses = productService.createProduct(productRequest);
        return ResponseEntity.ok(productResponses);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {
        List<ProductResponse> productResponses = productService.getAllProducts();
        return ResponseEntity.ok(productResponses);
    }
}
