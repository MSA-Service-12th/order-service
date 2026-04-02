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
public class OrderSummaryDto {

    private UUID orderId;
    private String supplierName;
    private String receiverName;
    private String itemName;
    private Integer quantity;

    private OrderStatus status;
    private LocalDateTime createdAt;

    public static OrderSummaryDto from(Order order) {
        return OrderSummaryDto.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus())
                .quantity(order.getOrderItem().getQuantity())
                .supplierName(order.getSupplier().getSupplierName())
                .receiverName(order.getReceiver().getReceiverName())
                .itemName(order.getOrderItem().getOrderItemInfo().getItemName())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
