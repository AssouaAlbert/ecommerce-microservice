package com.aassoua.inventory_service.Service;


import com.aassoua.inventory_service.dto.InventoryResponse;
import com.aassoua.inventory_service.model.Inventory;
import com.aassoua.inventory_service.repo.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> getStockBySkuCode(List<String> skuCodes) {

        Optional<List<Inventory>> inventoryStock = inventoryRepository.findBySkuCodeIn(skuCodes);

        List<InventoryResponse> inventoryResponseStock = inventoryStock
                .map(stocks -> stocks.stream()
                        .map(this::mapToInventoryResponse)
                        .collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);

        Set<String> foundSkuCodes = inventoryResponseStock.stream()
                .map(item -> item.getSkuCode())
                .collect(Collectors.toSet());

        List<InventoryResponse> notFoundResponsesStock = skuCodes.stream()
                .filter(skuCode -> !foundSkuCodes.contains(skuCode))
                .map(skuCode -> new InventoryResponse(skuCode, false)) // Assuming InventoryResponse constructor is: InventoryResponse(skuCode, isInStock)
                .collect(Collectors.toList());

        List<InventoryResponse> combinedResponses = new ArrayList<>(inventoryResponseStock);
        combinedResponses.addAll(notFoundResponsesStock);
        return combinedResponses;
    }

    @Transactional(readOnly = true)
    public Optional<InventoryResponse> getSingleStockBySkuCode(String skuCode) {
        Optional<Inventory> stock = inventoryRepository.findBySkuCode(skuCode);
        if (stock.isPresent()) {
            return stock.map(this::mapToInventoryResponse);
        }
        return Optional.of(new InventoryResponse(skuCode, false));
    }

    public InventoryResponse mapToInventoryResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .isInStock(inventory.getQuantity() > 0)
                .skuCode(inventory.getSkuCode())
                .build();
    }
}
