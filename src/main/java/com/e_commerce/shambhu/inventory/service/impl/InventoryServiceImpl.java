package com.e_commerce.shambhu.inventory.service.impl;

import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import com.e_commerce.shambhu.inventory.dto.request.*;
import com.e_commerce.shambhu.inventory.dto.response.InventoryMovementResponse;
import com.e_commerce.shambhu.inventory.dto.response.InventoryResponse;
import com.e_commerce.shambhu.inventory.entity.Inventory;
import com.e_commerce.shambhu.inventory.entity.InventoryMovement;
import com.e_commerce.shambhu.inventory.enums.MovementType;
import com.e_commerce.shambhu.inventory.repository.InventoryMovementRepository;
import com.e_commerce.shambhu.inventory.repository.InventoryRepository;
import com.e_commerce.shambhu.inventory.service.InventoryService;
import com.e_commerce.shambhu.product.entity.Product;
import com.e_commerce.shambhu.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepository inventoryRepository;

    private final InventoryMovementRepository inventoryMovementRepository;

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    // Optional
    private final EntityManager entityManager;

    @Override
    public InventoryResponse createInventory(
            CreateInventoryRequest request) {

        LOGGER.info(
                "Creating inventory for productId={}",
                request.getProductId()
        );

        Product product = productRepository.findById(request.getProductId())
                .filter(productData ->
                        !Boolean.TRUE.equals(productData.getDeleted()) &&
                                Boolean.TRUE.equals(productData.getActive()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found.", null, null
                ));

        if (inventoryRepository.existsByProduct(product)) {

            LOGGER.warn(
                    "Inventory already exists for productId={}",
                    product.getId()
            );

            throw new BusinessException(
                    "Inventory already exists for this product."
            );
        }

        validateStockLevels(
                request.getMinimumStockLevel(),
                request.getReorderLevel(),
                request.getMaximumStockLevel()
        );

        Inventory inventory = buildInventory(
                product,
                request
        );

        inventory = inventoryRepository.save(inventory);

        createInventoryMovement(
                inventory,
                MovementType.PURCHASE,
                request.getAvailableQuantity(),
                0,
                request.getAvailableQuantity(),
                null,
                "Initial inventory created."
        );

        LOGGER.info(
                "Inventory created successfully. InventoryId={}, ProductId={}",
                inventory.getId(),
                product.getId()
        );

        return mapToResponse(inventory);
    }

    @Override
    public InventoryResponse updateInventory(Long productId, UpdateInventoryRequest request) {
        return null;
    }

    @Override
    public InventoryResponse getInventory(Long productId) {
        return null;
    }

    @Override
    public InventoryResponse reserveStock(Long productId, ReserveStockRequest request) {
        return null;
    }

    @Override
    public InventoryResponse releaseStock(Long productId, ReleaseStockRequest request) {
        return null;
    }

    @Override
    public InventoryResponse deductStock(Long productId, DeductStockRequest request) {
        return null;
    }

    @Override
    public InventoryResponse addStock(Long productId, AddStockRequest request) {
        return null;
    }

    @Override
    public InventoryResponse adjustStock(Long productId, AdjustStockRequest request) {
        return null;
    }

    @Override
    public Page<InventoryMovementResponse> getInventoryMovements(Long productId, Pageable pageable) {
        return null;
    }

    private void validateStockLevels(
            Integer minimum,
            Integer reorder,
            Integer maximum) {

        if (minimum > reorder) {

            throw new BusinessException(
                    "Minimum stock level cannot be greater than reorder level."
            );
        }

        if (reorder > maximum) {

            throw new BusinessException(
                    "Reorder level cannot be greater than maximum stock level."
            );
        }
    }

    private Inventory buildInventory(
            Product product,
            CreateInventoryRequest request) {

        return Inventory.builder()
                .product(product)
                .availableQuantity(request.getAvailableQuantity())
                .reservedQuantity(0)
                .minimumStockLevel(request.getMinimumStockLevel())
                .maximumStockLevel(request.getMaximumStockLevel())
                .reorderLevel(request.getReorderLevel())
                .active(true)
                .deleted(false)
                .build();
    }

    private void createInventoryMovement(
            Inventory inventory,
            MovementType movementType,
            Integer quantity,
            Integer previousQuantity,
            Integer currentQuantity,
            Long referenceId,
            String remarks) {

        InventoryMovement movement = InventoryMovement.builder()
                .inventory(inventory)
                .movementType(movementType)
                .quantity(quantity)
                .previousQuantity(previousQuantity)
                .currentQuantity(currentQuantity)
                .referenceId(referenceId)
                .remarks(remarks)
                .build();

        inventoryMovementRepository.save(movement);
    }

    private InventoryResponse mapToResponse(
            Inventory inventory) {

        InventoryResponse response =
                modelMapper.map(
                        inventory,
                        InventoryResponse.class
                );

        response.setProductId(
                inventory.getProduct().getId()
        );

        response.setProductName(
                inventory.getProduct().getName()
        );

        return response;
    }
}
