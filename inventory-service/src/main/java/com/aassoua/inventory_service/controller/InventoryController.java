package com.aassoua.inventory_service.controller;

import com.aassoua.inventory_service.Service.InventoryService;
import com.aassoua.inventory_service.dto.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class InventoryController {

    public final InventoryService inventoryService;

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryResponse>> isInStock(@RequestParam List<String> skuCode) {
        List<InventoryResponse> stock = inventoryService.getStockBySkuCode(skuCode);
        return ResponseEntity.ok(stock);
    }

    @GetMapping("/inventory/{skuCode}")
    public ResponseEntity<InventoryResponse> isInStock(@PathVariable("skuCode") String skuCode) {
        Optional<InventoryResponse> stock = inventoryService.getSingleStockBySkuCode(skuCode);
        if (stock.isPresent()) {
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(stock.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
