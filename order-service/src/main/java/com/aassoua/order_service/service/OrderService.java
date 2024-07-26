package com.aassoua.order_service.service;


import com.aassoua.order_service.dto.OrderLineItemDto;
import com.aassoua.order_service.dto.OrderRequestDto;
import com.aassoua.order_service.model.Order;
import com.aassoua.order_service.model.OrderLineItem;
import com.aassoua.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {


    private final OrderRepository orderRepository;

    public Optional<Order> placeOrder(OrderRequestDto orderRequestDto) {

        Order order = mapToOrder(orderRequestDto);
        return Optional.of(orderRepository.save(order));
    }


    private OrderLineItem mapToOrderLineItem(OrderLineItemDto orderLineItemDto) {
        log.warn("Processing: " + orderLineItemDto);
        return OrderLineItem.builder().price(orderLineItemDto.getPrice())
                .skuCode(orderLineItemDto.getSkuCode())
                .quantity(orderLineItemDto.getQuantity())
                .build();

    }

    private Order mapToOrder(OrderRequestDto orderRequestDto) {
        return Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItemList(orderRequestDto.getOrderLineItemListDto()
                        .stream()
                        .map(this::mapToOrderLineItem)
                        .collect(Collectors.toList()))
                .totalPrice(orderRequestDto.getOrderLineItemListDto()
                        .stream()
                        .map(OrderLineItemDto::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();
    }

}
