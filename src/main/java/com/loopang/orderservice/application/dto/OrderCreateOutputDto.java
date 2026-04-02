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
public class OrderCreateOutputDto {

    // 주문ID
    private UUID orderId;

    // 공급업체 및 출발 허브 ID
    private UUID supplierId;
    private UUID supplierHubId;

    // 수령업체 및 도착 허브 ID
    private UUID receiverId;
    private UUID receiverHubId;

    // 상품ID 및 수량
    private UUID itemId;
    private Integer quantity;

    // 주문 진행 및 승인 정보(주문 승인 및 배송 진행 시 UUID값 반환)
    private OrderStatus status;

    private LocalDateTime createdAt;
    private String createdBy;

    public static OrderCreateOutputDto from(Order order) {
        return OrderCreateOutputDto.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus())
                .supplierId(order.getSupplier().getSupplierId())
                .supplierHubId(order.getSupplier().getHubId())
                .receiverId(order.getReceiver().getReceiverId())
                .receiverHubId(order.getReceiver().getHubId())
                .itemId(order.getOrderItem().getOrderItemInfo().getItemId())
                .quantity(order.getOrderItem().getQuantity())
                .createdAt(order.getCreatedAt())
                .createdBy(order.getCreatedBy() != null ? order.getCreatedBy().toString() : null)
                .build();
    }
}
