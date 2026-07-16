package com.e_commerce.shambhu.inventory.service;

import com.e_commerce.shambhu.inventory.dto.request.AddStockRequest;
import com.e_commerce.shambhu.inventory.dto.request.AdjustStockRequest;
import com.e_commerce.shambhu.inventory.dto.request.CreateInventoryRequest;
import com.e_commerce.shambhu.inventory.dto.request.DeductStockRequest;
import com.e_commerce.shambhu.inventory.dto.request.ReleaseStockRequest;
import com.e_commerce.shambhu.inventory.dto.request.ReserveStockRequest;
import com.e_commerce.shambhu.inventory.dto.request.UpdateInventoryRequest;
import com.e_commerce.shambhu.inventory.dto.response.InventoryMovementResponse;
import com.e_commerce.shambhu.inventory.dto.response.InventoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

    /**
     * Create inventory for a product.
     */
    InventoryResponse createInventory(
            CreateInventoryRequest request);

    /**
     * Update inventory configuration.
     */
    InventoryResponse updateInventory(
            Long productId,
            UpdateInventoryRequest request);

    /**
     * Retrieve inventory by product.
     */
    InventoryResponse getInventory(
            Long productId);

    /**
     * Reserve stock during checkout.
     */
    InventoryResponse reserveStock(
            Long productId,
            ReserveStockRequest request);

    /**
     * Release previously reserved stock.
     */
    InventoryResponse releaseStock(
            Long productId,
            ReleaseStockRequest request);

    /**
     * Deduct reserved stock after successful payment.
     */
    InventoryResponse deductStock(
            Long productId,
            DeductStockRequest request);

    /**
     * Add stock after procurement or return.
     */
    InventoryResponse addStock(
            Long productId,
            AddStockRequest request);

    /**
     * Manual stock adjustment.
     */
    InventoryResponse adjustStock(
            Long productId,
            AdjustStockRequest request);

    /**
     * Retrieve inventory movement history.
     */
    Page<InventoryMovementResponse> getInventoryMovements(
            Long productId,
            Pageable pageable);

    boolean isStockAvailable(Long productId, Integer quantity);
}
