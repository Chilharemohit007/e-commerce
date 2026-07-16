package com.e_commerce.shambhu.inventory.repository;

import com.e_commerce.shambhu.inventory.entity.Inventory;
import com.e_commerce.shambhu.inventory.entity.InventoryMovement;
import com.e_commerce.shambhu.inventory.enums.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryMovementRepository
        extends JpaRepository<InventoryMovement, Long> {

    @EntityGraph(attributePaths = "inventory")
    List<InventoryMovement> findByInventoryOrderByCreatedAtDesc(
            Inventory inventory
    );

    @EntityGraph(attributePaths = "inventory")
    Page<InventoryMovement> findByMovementType(
            MovementType movementType,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "inventory")
    Page<InventoryMovement> findByCreatedAtBetween(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "inventory")
    Page<InventoryMovement> findByInventory(
            Inventory inventory,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "inventory")
    Page<InventoryMovement> findByReferenceId(
            Long referenceId,
            Pageable pageable
    );

}
