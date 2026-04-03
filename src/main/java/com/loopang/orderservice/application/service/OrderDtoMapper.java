package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderDetailsDto;
import com.loopang.orderservice.application.dto.OrderSummaryDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.vo.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderDtoMapper {

    public OrderDetailsDto toDetailsDto(Order order) {
        Supplier supplier = order.getSupplier();
        Receiver receiver = order.getReceiver();
        OrderItem item = order.getOrderItem();
        Optional<HubManager> manager = Optional.ofNullable(order.getHubManager());

        return OrderDetailsDto.builder()
                // 주문 기본 정보
                .orderId(order.getOrderId())
                .status(order.getStatus())
                .deliveryId(order.getDeliveryId())

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

                // 담당 허브 관리자 정보 (HubManager VO)
                .hubChargeId(manager.map(HubManager::getHubChargeId).orElse(null))
                .hubChargeName(manager.map(HubManager::getHubChargeName).orElse(null))

                // 메타데이터
                .createdAt(order.getCreatedAt())
                .createdBy(order.getCreatedBy())
                .updatedAt(order.getUpdatedAt())
                .updatedBy(order.getUpdatedBy())
                .build();
    }

    public OrderSummaryDto toSummaryDto(Order order) {
        Supplier supplier = order.getSupplier();
        Receiver receiver = order.getReceiver();
        OrderItem item = order.getOrderItem();

        return OrderSummaryDto.builder()
                .orderId(order.getOrderId())
                .supplierId(supplier.getSupplierId())
                .supplierName(supplier.getSupplierName())
                .receiverId(receiver.getReceiverId())
                .receiverName(receiver.getReceiverName())
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .quantity(item.getQuantity())
                .status(order.getStatus())
                .build();
    }
}
