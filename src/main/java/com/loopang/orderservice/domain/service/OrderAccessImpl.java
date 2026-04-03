package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.vo.UserType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderAccessImpl implements OrderAccess {

    @Override
    public void validateCreateAccess(UserType userType) {
        if (userType == null || userType == UserType.PENDING) {
            throw new OrderException(OrderErrorCode.ORDER_ACCESS_DENIED);
        }
    }

    @Override
    public void validateListSearchAccess(UserType userType) {
        if (userType != UserType.MASTER) {
            throw new OrderException(OrderErrorCode.ORDER_ACCESS_DENIED);
        }
    }

    @Override
    public void validateReadAccess(UUID userId, UserType userType, UUID correlationId, Order order) {
        if (userType == UserType.MASTER) {
            return;
        }

        if (userType == UserType.HUB && order.isManagedByOrInitial(userId, correlationId)) {
            return;
        }

        if (userType == UserType.COMPANY && order.isCreatedBy(userId)) {
            return;
        }

        // 임시 로직: userId와 deliveryId가 존재하고, 주문에 할당된 배송 정보와 일치하는지 확인
        if (userType == UserType.DELIVERY
                && order.isAssignedToDelivery(correlationId)) {
            return;
        }

        throw new OrderException(OrderErrorCode.ORDER_ACCESS_DENIED);
    }

    @Override
    public void validateUpdateDeleteAccess(UUID userId, UserType userType, UUID correlationId, Order order) {
        if (userType == UserType.MASTER) {
            return;
        }

        if (userType == UserType.HUB && order.isManagedByOrInitial(userId, correlationId)) {
            return;
        }

        throw new OrderException(OrderErrorCode.ORDER_ACCESS_DENIED);
    }
}
