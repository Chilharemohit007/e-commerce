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

    @Override
    public InventoryResponse updateInventory(
            Long productId,
            UpdateInventoryRequest request) {

        LOGGER.info(
                "Updating inventory configuration for productId={}",
                productId
        );

        Inventory inventory = inventoryRepository
                .findByProductIdAndDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found.", null, null
                ));

        if (!Boolean.TRUE.equals(inventory.getActive())) {

            LOGGER.warn(
                    "Attempt to update inactive inventory. ProductId={}",
                    productId
            );

            throw new BusinessException(
                    "Inventory is inactive."
            );
        }

        validateStockLevels(
                request.getMinimumStockLevel(),
                request.getReorderLevel(),
                request.getMaximumStockLevel()
        );

        inventory.setMinimumStockLevel(
                request.getMinimumStockLevel()
        );

        inventory.setMaximumStockLevel(
                request.getMaximumStockLevel()
        );

        inventory.setReorderLevel(
                request.getReorderLevel()
        );

        inventory.setActive(
                request.getActive()
        );

        inventory = inventoryRepository.save(inventory);

        LOGGER.info(
                "Inventory configuration updated successfully. InventoryId={}, ProductId={}",
                inventory.getId(),
                productId
        );

        return mapToResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long productId) {

        LOGGER.info(
                "Fetching inventory for productId={}",
                productId
        );

        Inventory inventory = inventoryRepository
                .findByProductIdAndDeletedFalse(productId)
                .orElseThrow(() -> {

                    LOGGER.warn(
                            "Inventory not found for productId={}",
                            productId
                    );

                    return new ResourceNotFoundException(
                            "Inventory not found.", null, null
                    );
                });

        LOGGER.info(
                "Inventory fetched successfully. InventoryId={}, ProductId={}",
                inventory.getId(),
                productId
        );

        return mapToResponse(inventory);
    }

    @Override
    public InventoryResponse reserveStock(
            Long productId,
            ReserveStockRequest request) {

        LOGGER.info(
                "Reserving {} unit(s) for productId={}",
                request.getQuantity(),
                productId
        );

        Inventory inventory = inventoryRepository
                .findByProductIdAndDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found.", null, null
                ));

        if (!Boolean.TRUE.equals(inventory.getActive())) {

            LOGGER.warn(
                    "Inventory is inactive. ProductId={}",
                    productId
            );

            throw new BusinessException(
                    "Inventory is inactive."
            );
        }

        if (inventory.getAvailableQuantity() < request.getQuantity()) {

            LOGGER.warn(
                    "Insufficient stock. ProductId={}, Available={}, Requested={}",
                    productId,
                    inventory.getAvailableQuantity(),
                    request.getQuantity()
            );

            throw new BusinessException(
                    "Insufficient stock available."
            );
        }

        Integer previousQuantity = inventory.getAvailableQuantity();

        inventory.setAvailableQuantity(
                previousQuantity - request.getQuantity()
        );

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() + request.getQuantity()
        );

        inventory = inventoryRepository.save(inventory);

        createInventoryMovement(
                inventory,
                MovementType.RESERVATION,
                request.getQuantity(),
                previousQuantity,
                inventory.getAvailableQuantity(),
                request.getReferenceId(),
                request.getRemarks()
        );

        LOGGER.info(
                "Stock reserved successfully. ProductId={}, Reserved={}, Available={}",
                productId,
                inventory.getReservedQuantity(),
                inventory.getAvailableQuantity()
        );

        return mapToResponse(inventory);
    }

    @Override
    public InventoryResponse releaseStock(
            Long productId,
            ReleaseStockRequest request) {

        LOGGER.info(
                "Releasing {} unit(s) for productId={}",
                request.getQuantity(),
                productId
        );

        Inventory inventory = inventoryRepository
                .findByProductIdAndDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found.", null, null
                ));

        if (!Boolean.TRUE.equals(inventory.getActive())) {

            LOGGER.warn(
                    "Inventory is inactive. ProductId={}",
                    productId
            );

            throw new BusinessException(
                    "Inventory is inactive."
            );
        }

        if (inventory.getReservedQuantity() < request.getQuantity()) {

            LOGGER.warn(
                    "Insufficient reserved stock. ProductId={}, Reserved={}, Requested={}",
                    productId,
                    inventory.getReservedQuantity(),
                    request.getQuantity()
            );

            throw new BusinessException(
                    "Insufficient reserved stock."
            );
        }

        Integer previousQuantity = inventory.getAvailableQuantity();

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - request.getQuantity()
        );

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() + request.getQuantity()
        );

        inventory = inventoryRepository.save(inventory);

        createInventoryMovement(
                inventory,
                MovementType.RELEASE,
                request.getQuantity(),
                previousQuantity,
                inventory.getAvailableQuantity(),
                request.getReferenceId(),
                request.getRemarks()
        );

        LOGGER.info(
                "Stock released successfully. ProductId={}, Available={}, Reserved={}",
                productId,
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );

        return mapToResponse(inventory);
    }

    @Override
    public InventoryResponse deductStock(
            Long productId,
            DeductStockRequest request) {

        LOGGER.info(
                "Deducting {} unit(s) for productId={}",
                request.getQuantity(),
                productId
        );

        Inventory inventory = inventoryRepository
                .findByProductIdAndDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found.", null, null
                ));

        if (!Boolean.TRUE.equals(inventory.getActive())) {

            LOGGER.warn(
                    "Inventory is inactive. ProductId={}",
                    productId
            );

            throw new BusinessException(
                    "Inventory is inactive."
            );
        }

        if (inventory.getReservedQuantity() < request.getQuantity()) {

            LOGGER.warn(
                    "Insufficient reserved stock. ProductId={}, Reserved={}, Requested={}",
                    productId,
                    inventory.getReservedQuantity(),
                    request.getQuantity()
            );

            throw new BusinessException(
                    "Insufficient reserved stock."
            );
        }

        Integer previousQuantity = inventory.getAvailableQuantity();

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - request.getQuantity()
        );

        inventory = inventoryRepository.save(inventory);

        createInventoryMovement(
                inventory,
                MovementType.SALE,
                request.getQuantity(),
                previousQuantity,
                inventory.getAvailableQuantity(),
                request.getReferenceId(),
                request.getRemarks()
        );

        LOGGER.info(
                "Stock deducted successfully. ProductId={}, Available={}, Reserved={}",
                productId,
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );

        return mapToResponse(inventory);
    }

    @Override
    public InventoryResponse addStock(
            Long productId,
            AddStockRequest request) {

        LOGGER.info(
                "Adding {} unit(s) to inventory. ProductId={}",
                request.getQuantity(),
                productId
        );

        Inventory inventory = inventoryRepository
                .findByProductIdAndDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found.", null, null
                ));

        if (!Boolean.TRUE.equals(inventory.getActive())) {

            LOGGER.warn(
                    "Inventory is inactive. ProductId={}",
                    productId
            );

            throw new BusinessException(
                    "Inventory is inactive."
            );
        }

        Integer previousQuantity = inventory.getAvailableQuantity();
        Integer newQuantity = previousQuantity + request.getQuantity();

        /*
         * Business Rule:
         * Prevent stock from exceeding the configured maximum level.
         * If your business allows exceeding the maximum,
         * replace this exception with a warning log.
         */
        if (newQuantity > inventory.getMaximumStockLevel()) {

            LOGGER.warn(
                    "Maximum stock level exceeded. ProductId={}, Maximum={}, Attempted={}",
                    productId,
                    inventory.getMaximumStockLevel(),
                    newQuantity
            );

            throw new BusinessException(
                    "Adding stock exceeds the maximum stock level."
            );
        }

        inventory.setAvailableQuantity(newQuantity);

        inventory = inventoryRepository.save(inventory);

        createInventoryMovement(
                inventory,
                MovementType.PURCHASE,
                request.getQuantity(),
                previousQuantity,
                newQuantity,
                request.getReferenceId(),
                request.getRemarks()
        );

        LOGGER.info(
                "Stock added successfully. ProductId={}, Available={}",
                productId,
                inventory.getAvailableQuantity()
        );

        return mapToResponse(inventory);
    }

    @Override
    public InventoryResponse adjustStock(
            Long productId,
            AdjustStockRequest request) {

        LOGGER.info(
                "Adjusting inventory for productId={}, NewQuantity={}",
                productId,
                request.getQuantity()
        );

        Inventory inventory = inventoryRepository
                .findByProductIdAndDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found.", null, null
                ));

        if (!Boolean.TRUE.equals(inventory.getActive())) {

            LOGGER.warn(
                    "Inventory is inactive. ProductId={}",
                    productId
            );

            throw new BusinessException(
                    "Inventory is inactive."
            );
        }

        if (request.getQuantity() < 0) {

            throw new BusinessException(
                    "Inventory quantity cannot be negative."
            );
        }

        /*
         * Available stock cannot be less than
         * the quantity already reserved.
         */
        if (request.getQuantity() < inventory.getReservedQuantity()) {

            throw new BusinessException(
                    "Adjusted quantity cannot be less than reserved quantity."
            );
        }

        Integer previousQuantity = inventory.getAvailableQuantity();

        inventory.setAvailableQuantity(
                request.getQuantity()
        );

        inventory = inventoryRepository.save(inventory);

        createInventoryMovement(
                inventory,
                MovementType.ADJUSTMENT,
                Math.abs(request.getQuantity() - previousQuantity),
                previousQuantity,
                request.getQuantity(),
                null,
                request.getRemarks()
        );

        LOGGER.info(
                "Inventory adjusted successfully. ProductId={}, Previous={}, Current={}",
                productId,
                previousQuantity,
                request.getQuantity()
        );

        return mapToResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryMovementResponse> getInventoryMovements(
            Long productId,
            Pageable pageable) {

        LOGGER.info(
                "Fetching inventory movements for productId={}, page={}, size={}",
                productId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Inventory inventory = inventoryRepository
                .findByProductIdAndDeletedFalse(productId)
                .orElseThrow(() -> {

                    LOGGER.warn(
                            "Inventory not found for productId={}",
                            productId
                    );

                    return new ResourceNotFoundException(
                            "Inventory not found.", null, null
                    );
                });

        Page<InventoryMovement> movements =
                inventoryMovementRepository.findByInventory(
                        inventory,
                        pageable
                );

        LOGGER.info(
                "Retrieved {} inventory movement(s) for productId={}",
                movements.getNumberOfElements(),
                productId
        );

        return movements.map(this::mapToInventoryMovementResponse);
    }

    @Override
    public boolean isStockAvailable(Long productId, Integer quantity) {
        return false;
    }

    private InventoryMovementResponse mapToInventoryMovementResponse(
            InventoryMovement movement) {

        InventoryMovementResponse response =
                modelMapper.map(
                        movement,
                        InventoryMovementResponse.class
                );

        response.setInventoryId(
                movement.getInventory().getId()
        );

        return response;
    }
}
