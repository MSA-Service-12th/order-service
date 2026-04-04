package com.loopang.orderservice.application.dto;

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
public class OrderDetailsDto {

    private UUID orderId;

    private UUID supplierId;
    private String supplierName;
    private UUID supplierHubId;
    private String supplierHubName;

    private UUID receiverId;
    private String receiverName;
    private UUID receiverHubId;
    private String receiverHubName;
    private String requirements;

    private UUID itemId;
    private String itemName;
    private Integer quantity;

    private UUID deliveryId;
    private UUID hubChargeId;
    private String hubChargeName;
    private OrderStatus status;

    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
}
