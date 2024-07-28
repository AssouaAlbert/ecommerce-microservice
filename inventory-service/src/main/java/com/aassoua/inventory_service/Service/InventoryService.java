package com.aassoua.inventory_service.Service;


import com.aassoua.inventory_service.model.Inventory;
import com.aassoua.inventory_service.repo.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    @Transactional(readOnly = true)
    public Boolean getStockBySkuCode(String skuCode) {
        return inventoryRepository.findBySkuCode(skuCode).isPresent();
    }
}
