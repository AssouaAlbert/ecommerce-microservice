package com.aassoua.productservice.service;

import com.aassoua.productservice.dto.ProductRequest;
import com.aassoua.productservice.dto.ProductResponse;
import com.aassoua.productservice.model.Product;
import com.aassoua.productservice.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepo productRepo;

    @Transactional
    public List<ProductResponse> createProduct(List<ProductRequest> productRequests) {
        List<Product> products = productRequests
                .stream()
                .map(this::mapToProduct)
                .collect(Collectors.toList());
        List<ProductResponse> productResponses = productRepo.saveAll(products)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
        productResponses.forEach(product -> log.info("Product {} is saved", product.getId()));
        return productResponses;
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepo.findAll();
        return products.stream().map(this::mapToProductResponse).collect(Collectors.toList());
    }

    public Product mapToProduct(ProductRequest productRequest) {
        return Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
    }

    public ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
