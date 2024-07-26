package com.aassoua.inventory_service.controller;

import com.aassoua.inventory_service.Service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    public final InventoryService inventoryService;

    @GetMapping("/{skuCode}")
    public ResponseEntity<Boolean> isInStock(@PathVariable("skuCode") String skuCode) {
        Boolean stock = inventoryService.getStockBySkuCode(skuCode);
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(stock);
    }
}
