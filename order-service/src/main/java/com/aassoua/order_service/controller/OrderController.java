package com.aassoua.order_service.controller;

import com.aassoua.order_service.dto.OrderRequestDto;
import com.aassoua.order_service.model.Order;
import com.aassoua.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequestDto orderRequestDto) {
        Optional<Order> orderOptional = orderService.placeOrder(orderRequestDto);

        if (orderOptional.isPresent()) {
            Order orderCreated = orderOptional.get();
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(orderCreated.getId())
                    .toUri();
            return ResponseEntity.created(location).body(orderCreated);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }
}
