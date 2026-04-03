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
public class OrderCreateResultDto {

    private UUID orderId;
    private UUID supplierId;
    private UUID supplierHubId;
    private UUID receiverId;
    private UUID receiverHubId;
    private UUID itemId;
    private Integer quantity;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private UUID createdBy;
}
