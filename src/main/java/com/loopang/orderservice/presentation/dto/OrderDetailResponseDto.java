package com.loopang.orderservice.presentation.dto;

import com.loopang.orderservice.application.dto.OrderDetailsDto;
import com.loopang.orderservice.domain.vo.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDetailResponseDto {
    private UUID orderId;
    private UUID supplierId;
    private String supplierName;
    private UUID supplierHubId;
    private String supplierHubName;
    private String requirements;
    private UUID receiverId;
    private String receiverName;
    private UUID receiverHubId;
    private String receiverHubName;
    private UUID itemId;
    private String itemName;
    private Integer quantity;
    private OrderStatus status;
    private UUID deliveryId;
    private UUID hubChargeId;
    private String hubChargeName;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;

    public static OrderDetailResponseDto from(OrderDetailsDto details) {
        return OrderDetailResponseDto.builder()
                .orderId(details.getOrderId())
                .supplierId(details.getSupplierId())
                .supplierName(details.getSupplierName())
                .supplierHubId(details.getSupplierHubId())
                .supplierHubName(details.getSupplierHubName())
                .requirements(details.getRequirements())
                .receiverId(details.getReceiverId())
                .receiverName(details.getReceiverName())
                .receiverHubId(details.getReceiverHubId())
                .receiverHubName(details.getReceiverHubName())
                .itemId(details.getItemId())
                .itemName(details.getItemName())
                .quantity(details.getQuantity())
                .status(details.getStatus())
                .deliveryId(details.getDeliveryId())
                .hubChargeId(details.getHubChargeId())
                .hubChargeName(details.getHubChargeName())
                .createdAt(details.getCreatedAt())
                .createdBy(details.getCreatedBy())
                .updatedAt(details.getUpdatedAt())
                .updatedBy(details.getUpdatedBy())
                .build();
    }
}
