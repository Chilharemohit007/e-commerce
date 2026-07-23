package com.e_commerce.shambhu.shipment.service.impl;

import com.e_commerce.shambhu.shipment.dto.request.AssignTrackingRequest;
import com.e_commerce.shambhu.shipment.dto.request.CreateShipmentRequest;
import com.e_commerce.shambhu.shipment.dto.request.UpdateShipmentStatusRequest;
import com.e_commerce.shambhu.shipment.dto.response.ShipmentResponse;
import com.e_commerce.shambhu.shipment.dto.response.ShipmentSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShipmentService {

    /**
     * Creates a shipment for an order.
     *
     * @param request shipment creation request
     * @return created shipment details
     */
    ShipmentResponse createShipment(CreateShipmentRequest request);

    /**
     * Retrieves shipment details by shipment ID.
     *
     * @param shipmentId shipment identifier
     * @return shipment details
     */
    ShipmentResponse getShipment(Long shipmentId);

    /**
     * Retrieves shipment details by order ID.
     *
     * @param orderId order identifier
     * @return shipment details
     */
    ShipmentResponse getShipmentByOrder(Long orderId);

    /**
     * Retrieves shipment history of the authenticated customer.
     *
     * @param pageable pagination information
     * @return paginated shipment history
     */
    Page<ShipmentSummaryResponse> getMyShipments(Pageable pageable);

    /**
     * Retrieves all shipments.
     * Intended for ADMIN users.
     *
     * @param pageable pagination information
     * @return paginated shipment list
     */
    Page<ShipmentSummaryResponse> getAllShipments(Pageable pageable);

    /**
     * Updates shipment status.
     *
     * @param request status update request
     * @return updated shipment details
     */
    ShipmentResponse updateShipmentStatus(
            UpdateShipmentStatusRequest request);

    /**
     * Assigns courier tracking information.
     *
     * @param request tracking assignment request
     * @return updated shipment details
     */
    ShipmentResponse assignTracking(
            AssignTrackingRequest request);

    /**
     * Cancels a shipment.
     *
     * @param shipmentId shipment identifier
     * @return updated shipment details
     */
    ShipmentResponse cancelShipment(Long shipmentId);

}
