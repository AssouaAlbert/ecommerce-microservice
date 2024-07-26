package com.aassoua.order_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "t_order_line_items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50) // Optional: specify constraints for SKU code
    private String skuCode;

    @Column(nullable = false, precision = 19, scale = 4) // Optional: specify precision and scale for BigDecimal
    private BigDecimal price;

    @Column(nullable = false) // Optional: specify if the quantity can be null
    private Integer quantity = 1;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}