package com.aassoua.inventory_service.Service;


import com.aassoua.inventory_service.dto.InventoryResponse;
import com.aassoua.inventory_service.model.Inventory;
import com.aassoua.inventory_service.repo.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> getStockBySkuCode(List<String> skuCode) {
        Optional<List<Inventory>> stocksOptional = inventoryRepository.findBySkuCodeIn(skuCode);

        return stocksOptional
                .map(stocks -> stocks.stream()
                        .map(this::mapToInventoryResponse)
                        .collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }

    public Optional<InventoryResponse> getSingleStockBySkuCode(String skuCode) {
        Optional<Inventory> stock = inventoryRepository.findBySkuCode(skuCode);
        return stock.map(this::mapToInventoryResponse);
    }

    public InventoryResponse mapToInventoryResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .isInStock(inventory.getQuantity() > 0)
                .skuCode(inventory.getSkuCode())
                .build();
    }
}
