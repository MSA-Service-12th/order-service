package com.loopang.orderservice.application.dto;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.vo.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDetailsDto {

    private UUID orderId;

    private UUID supplierId;
    private String supplierName;

    private UUID itemId;
    private String itemName;
    private Integer quantity;
    private String requirements;

    private UUID receiverId;
    private String receiverName;

    private OrderStatus status;

    private UUID deliveryId;
    private UUID hubChargeId;
    private String hubChargeName;

    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;

    public static OrderDetailsDto from(Order order) {
        return OrderDetailsDto.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus())
                .quantity(order.getOrderItem().getQuantity())
                .requirements(order.getSupplier().getRequirements())
                .deliveryId(order.getDeliveryId())
                .supplierId(order.getSupplier().getSupplierId())
                .supplierName(order.getSupplier().getSupplierName())
                .receiverId(order.getReceiver().getReceiverId())
                .receiverName(order.getReceiver().getReceiverName())
                .itemId(order.getOrderItem().getOrderItemInfo().getItemId())
                .itemName(order.getOrderItem().getOrderItemInfo().getItemName())
                .hubChargeId(order.getHubManager() != null ? order.getHubManager().getHubChargeId() : null)
                .hubChargeName(order.getHubManager() != null ? order.getHubManager().getHubChargeName() : null)
                .createdAt(order.getCreatedAt())
                .createdBy(order.getCreatedBy())
                .updatedAt(order.getUpdatedAt())
                .updatedBy(order.getUpdatedBy())
                .build();
    }
}
