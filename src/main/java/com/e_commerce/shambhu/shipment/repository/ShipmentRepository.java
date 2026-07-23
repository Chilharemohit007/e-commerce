package com.e_commerce.shambhu.shipment.repository;

import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.shipment.entity.Shipment;
import com.e_commerce.shambhu.shipment.enums.DeliveryPartner;
import com.e_commerce.shambhu.shipment.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    /**
     * Fetch shipment by order.
     */
    Optional<Shipment> findByOrder(Order order);

    /**
     * Fetch shipment by order id.
     */
    Optional<Shipment> findByOrderId(Long orderId);

    /**
     * Fetch shipment using internal tracking number.
     */
    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    /**
     * Fetch shipment using courier tracking number.
     */
    Optional<Shipment> findByCourierTrackingNumber(String courierTrackingNumber);

    /**
     * Customer shipment history.
     */
    @EntityGraph(attributePaths = {"order"})
    Page<Shipment> findByOrderUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable);

    /**
     * Filter shipments by status.
     */
    Page<Shipment> findByShipmentStatus(
            ShipmentStatus shipmentStatus,
            Pageable pageable);

    /**
     * Filter shipments by delivery partner.
     */
    Page<Shipment> findByDeliveryPartner(
            DeliveryPartner deliveryPartner,
            Pageable pageable);

    /**
     * Shipments expected today.
     */
    List<Shipment> findByEstimatedDeliveryDate(
            LocalDate estimatedDeliveryDate);

    /**
     * Shipments delivered today.
     */
    List<Shipment> findByActualDeliveryDate(
            LocalDate actualDeliveryDate);

    /**
     * Admin shipment listing.
     */
    @EntityGraph(attributePaths = {"order"})
    Page<Shipment> findAll(Pageable pageable);

    /**
     * Check shipment already exists.
     */
    boolean existsByOrderId(Long orderId);

}
