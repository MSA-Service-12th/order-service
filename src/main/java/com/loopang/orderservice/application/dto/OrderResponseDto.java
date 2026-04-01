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
public class OrderResponseDto {

    private UUID orderId;
    private OrderStatus status;
    
    // 공급업체 정보
    private UUID supplierId;
    private String supplierName;
    
    // 수령업체 정보
    private UUID receiverId;
    private String receiverName;
    
    // 상품 및 수량 정보
    private UUID itemId;
    private String itemName;
    private Integer quantity;
    
    private String requirements;
    private UUID deliveryId;
    
    private LocalDateTime createdAt;
    private String createdBy;

    public static OrderResponseDto from(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus())
                .supplierId(order.getSupplier().getSupplierId())
                .supplierName(order.getSupplier().getSupplierName())
                .receiverId(order.getReceiver().getReceiverId())
                .receiverName(order.getReceiver().getReceiverName())
                .itemId(order.getOrderItem().getOrderItemInfo().getItemId())
                .itemName(order.getOrderItem().getOrderItemInfo().getItemName())
                .quantity(order.getOrderItem().getQuantity())
                .requirements(order.getSupplier().getRequirements())
                .deliveryId(order.getDeliveryId())
                .createdAt(order.getCreatedAt())
                .createdBy(order.getCreatedBy() != null ? order.getCreatedBy().toString() : null)
                .build();
    }
}
