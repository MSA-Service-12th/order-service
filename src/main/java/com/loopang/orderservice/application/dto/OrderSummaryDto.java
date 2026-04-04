package com.loopang.orderservice.application.dto;

import com.loopang.orderservice.domain.vo.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSummaryDto {

    private UUID orderId;
    private UUID supplierId;
    private String supplierName;
    private UUID receiverId;
    private String receiverName;
    private UUID itemId;
    private String itemName;
    private Integer quantity;
    private OrderStatus status;
}
