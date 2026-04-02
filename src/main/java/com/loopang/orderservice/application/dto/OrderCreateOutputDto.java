package com.loopang.orderservice.application.dto;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.vo.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

// TODO: DTO 필드 구성은 수정 예정
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCreateOutputDto {

    private UUID orderId;
    private OrderStatus status;

    // 공급업체 및 출발 허브 정보
    private UUID supplierId;
    private String supplierName;
    private UUID supplierHubId;
    private String supplierHubName;

    // 수령업체 및 도착 허브 정보
    private UUID receiverId;
    private String receiverName;
    private String receiverAddress;
    private UUID receiverHubId;
    private String receiverHubName;

    // 상품 및 수량 정보
    private UUID itemId;
    private String itemName;
    private Integer quantity;
    private Integer itemNumber;

    // 주문 진행 및 승인 정보
    private String requirements;
    private UUID hubChargeId;   // 주문을 승인한 허브관리자 ID
    private String hubChargeName; // 주문을 승인한 허브관리자 이름
    private UUID deliveryId;

    private LocalDateTime createdAt;
    private String createdBy;

    public static OrderCreateOutputDto from(Order order) {
        return OrderCreateOutputDto.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus())
                // 공급업체 정보 평탄화 (안전한 getHubId() 활용)
                .supplierId(order.getSupplier().getSupplierId())
                .supplierName(order.getSupplier().getSupplierName())
                .supplierHubId(order.getSupplier().getHubId())
                .supplierHubName(order.getSupplier().getHubInfo() != null ? order.getSupplier().getHubInfo().getHubName() : null)
                // 수령업체 정보 평탄화
                .receiverId(order.getReceiver().getReceiverId())
                .receiverName(order.getReceiver().getReceiverName())
                .receiverAddress(order.getReceiver().getAddress())
                .receiverHubId(order.getReceiver().getHubId())
                .receiverHubName(order.getReceiver().getHubInfo() != null ? order.getReceiver().getHubInfo().getHubName() : null)
                // 상품 정보 평탄화
                .itemId(order.getOrderItem().getOrderItemInfo().getItemId())
                .itemName(order.getOrderItem().getOrderItemInfo().getItemName())
                .quantity(order.getOrderItem().getQuantity())
                .itemNumber(order.getOrderItem().getItemNumber())
                // 승인자 정보 (PENDING 상태 등에서는 null일 수 있으므로 안전하게 처리)
                .hubChargeId(order.getHubManager() != null ? order.getHubManager().getHubChargeId() : null)
                .hubChargeName(order.getHubManager() != null ? order.getHubManager().getHubChargeName() : null)
                .requirements(order.getSupplier().getRequirements())
                .deliveryId(order.getDeliveryId())
                .createdAt(order.getCreatedAt())
                .createdBy(order.getCreatedBy() != null ? order.getCreatedBy().toString() : null)
                .build();
    }
}
