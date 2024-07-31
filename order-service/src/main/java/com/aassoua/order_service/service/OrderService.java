package com.aassoua.order_service.service;


import com.aassoua.inventory_service.Service.InventoryService;
import com.aassoua.inventory_service.dto.InventoryResponse;
import com.aassoua.order_service.dto.OrderLineItemDto;
import com.aassoua.order_service.dto.OrderRequestDto;
import com.aassoua.order_service.model.Order;
import com.aassoua.order_service.model.OrderLineItem;
import com.aassoua.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {


    private final OrderRepository orderRepository;
    public final InventoryService inventoryService;
    public final WebClient webClient;


    @Value("${INVENTORY_SERVICE_PORT}")
    public final String inventoryPort;

    @Value("${BASE_URL}")
    public final String baseUrl;

    public Optional<Order> placeOrder(OrderRequestDto orderRequestDto) {

        Order order = mapToOrder(orderRequestDto);

        String pathVariable = order.getOrderLineItemList()
                .stream()
                .map(OrderLineItem::getSkuCode)
                .reduce("", (accumulator, skuCode) -> accumulator + "skuCode" + skuCode + "&");

        if (!pathVariable.isEmpty()) {
            pathVariable = pathVariable.substring(0, pathVariable.length() - 1);
            String serviceBUrl = baseUrl + ":" + "8082" + "/inventory?" + pathVariable;
            webClient
                    .get()
                    .uri(serviceBUrl)
                    .retrieve()
                    .bodyToMono(InventoryResponse.class)
                    .subscribe(stock -> {
                        // Process the stock value here
                        System.out.println("Stock availability: " + stock);

                    }, error -> {
                        // Handle errors here
                        System.err.println("Error occurred: " + error.getMessage());
                    });
        }

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
