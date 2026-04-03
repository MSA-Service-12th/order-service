package com.loopang.orderservice.application.dto;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.vo.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
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

    private UUID itemId;
    private String itemName;
    private Integer quantity;

    private UUID deliveryId;
    private UUID hubChargeId;
    private String hubChargeName;
    private OrderStatus status;

    private String requirements;

    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;

    public static OrderDetailsDto from(Order order) {
        Supplier supplier = order.getSupplier();
        Receiver receiver = order.getReceiver();
        OrderItem item = order.getOrderItem();
        Optional<HubManager> manager = Optional.ofNullable(order.getHubManager());

        return OrderDetailsDto.builder()
                // 주문 기본 정보
                .orderId(order.getOrderId())

                // 공급업체 정보 (Supplier VO)
                .supplierId(supplier.getSupplierId())
                .supplierName(supplier.getSupplierName())
                .supplierHubId(supplier.getHubId())
                .supplierHubName(supplier.getHubName())
                .requirements(supplier.getRequirements())

                // 수령업체 정보 (Receiver VO)
                .receiverId(receiver.getReceiverId())
                .receiverName(receiver.getReceiverName())
                .receiverHubId(receiver.getHubId())
                .receiverHubName(receiver.getHubName())

                // 상품 정보 (OrderItem VO)
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .quantity(item.getQuantity())

                // 주문 상태 & 주문 처리 담당자
                .status(order.getStatus())
                .deliveryId(order.getDeliveryId())
                .hubChargeId(manager.map(HubManager::getHubChargeId).orElse(null))
                .hubChargeName(manager.map(HubManager::getHubChargeName).orElse(null))

                // 메타데이터
                .createdAt(order.getCreatedAt())
                .createdBy(order.getCreatedBy())
                .updatedAt(order.getUpdatedAt())
                .updatedBy(order.getUpdatedBy())
                .build();
    }
}
