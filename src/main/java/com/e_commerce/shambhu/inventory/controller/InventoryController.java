package com.e_commerce.shambhu.inventory.controller;

import com.e_commerce.shambhu.common.response.ApiResponse;
import com.e_commerce.shambhu.common.response.ResponseBuilder;
import com.e_commerce.shambhu.inventory.dto.request.*;
import com.e_commerce.shambhu.inventory.dto.response.InventoryMovementResponse;
import com.e_commerce.shambhu.inventory.dto.response.InventoryResponse;
import com.e_commerce.shambhu.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Create Inventory
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryResponse>> createInventory(
            @Valid @RequestBody CreateInventoryRequest request) {

        return ResponseBuilder.created(
                "Inventory created successfully.",
                inventoryService.createInventory(request)
        );
    }

    /**
     * Update Inventory Configuration
     */
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateInventory(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateInventoryRequest request) {

        return ResponseBuilder.ok(
                "Inventory updated successfully.",
                inventoryService.updateInventory(productId, request)
        );
    }

    /**
     * Get Inventory
     */
    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(
            @PathVariable Long productId) {

        return ResponseBuilder.ok(
                "Inventory fetched successfully.",
                inventoryService.getInventory(productId)
        );
    }

    /**
     * Reserve Stock
     */
    @PostMapping("/{productId}/reserve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryResponse>> reserveStock(
            @PathVariable Long productId,
            @Valid @RequestBody ReserveStockRequest request) {

        return ResponseBuilder.ok(
                "Stock reserved successfully.",
                inventoryService.reserveStock(productId, request)
        );
    }

    /**
     * Release Stock
     */
    @PostMapping("/{productId}/release")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryResponse>> releaseStock(
            @PathVariable Long productId,
            @Valid @RequestBody ReleaseStockRequest request) {

        return ResponseBuilder.ok(
                "Reserved stock released successfully.",
                inventoryService.releaseStock(productId, request)
        );
    }

    /**
     * Deduct Stock
     */
    @PostMapping("/{productId}/deduct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryResponse>> deductStock(
            @PathVariable Long productId,
            @Valid @RequestBody DeductStockRequest request) {

        return ResponseBuilder.ok(
                "Stock deducted successfully.",
                inventoryService.deductStock(productId, request)
        );
    }

    /**
     * Add Stock
     */
    @PostMapping("/{productId}/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryResponse>> addStock(
            @PathVariable Long productId,
            @Valid @RequestBody AddStockRequest request) {

        return ResponseBuilder.ok(
                "Stock added successfully.",
                inventoryService.addStock(productId, request)
        );
    }

    /**
     * Adjust Stock
     */
    @PostMapping("/{productId}/adjust")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryResponse>> adjustStock(
            @PathVariable Long productId,
            @Valid @RequestBody AdjustStockRequest request) {

        return ResponseBuilder.ok(
                "Stock adjusted successfully.",
                inventoryService.adjustStock(productId, request)
        );
    }

    /**
     * Inventory Movement History
     */
    @GetMapping("/{productId}/movements")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Page<InventoryMovementResponse>>> getInventoryMovements(
            @PathVariable Long productId,
            Pageable pageable) {

        return ResponseBuilder.ok(
                "Inventory movements fetched successfully.",
                inventoryService.getInventoryMovements(
                        productId,
                        pageable
                )
        );
    }

}