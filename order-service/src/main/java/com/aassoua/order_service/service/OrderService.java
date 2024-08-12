package com.aassoua.order_service.service;


import com.aassoua.inventory_service.dto.InventoryResponse;
import com.aassoua.order_service.dto.OrderLineItemDto;
import com.aassoua.order_service.dto.OrderRequestDto;
import com.aassoua.order_service.model.Order;
import com.aassoua.order_service.model.OrderLineItem;
import com.aassoua.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {


    private final OrderRepository orderRepository;
    public final WebClient webClient;

    @Transactional
    public Optional<Order> placeOrder(OrderRequestDto orderRequestDto) {


        Boolean inStock = false;
        Order savedOrder, newOrder = null;

        List<String> skuCodes = orderRequestDto.getOrderLineItemListDto()
                .stream()
                .map(OrderLineItemDto::getSkuCode).collect(Collectors.toList());

        if (!skuCodes.isEmpty()) {
            String serviceBUrl = "http://localhost:8082/inventory";
            List<InventoryResponse> inventoryResponseStocks = webClient
                    .get()
                    .uri(serviceBUrl, uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToFlux(InventoryResponse.class)
                    .collectList()
                    .block();
            if (inventoryResponseStocks != null) {
                inStock = inventoryResponseStocks.stream().allMatch(InventoryResponse::isInStock);
            }
            if (inStock) {
                newOrder = mapToOrder(orderRequestDto);
                savedOrder = orderRepository.save(newOrder);
                return Optional.ofNullable(savedOrder);
            }
        }
        return Optional.ofNullable(newOrder);
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
