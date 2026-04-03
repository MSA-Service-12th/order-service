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
        if (userType == UserType.PENDING) {
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
    public void validateReadAccess(UUID userId, UserType userType, UUID managedHubId, Order order) {
        if (userType == UserType.MASTER) return;

        if (userType == UserType.HUB && order.isManagedByOrInitial(userId, managedHubId)) return;

        if (userType == UserType.COMPANY && order.isCreatedBy(userId)) return;

        throw new OrderException(OrderErrorCode.ORDER_ACCESS_DENIED);
    }

    @Override
    public void validateReadAccess(UserType userType, UUID deliveryId, Order order) {
        if (userType == UserType.MASTER) return;

        if (userType == UserType.DELIVERY && order.isAssignedToDelivery(deliveryId)) return;

        throw new OrderException(OrderErrorCode.ORDER_ACCESS_DENIED);
    }

    @Override
    public void validateUpdateDeleteAccess(UUID userId, UserType userType, UUID managedHubId, Order order) {
        if (userType == UserType.MASTER) return;

        if (userType == UserType.HUB && order.isManagedByOrInitial(userId, managedHubId)) return;

        throw new OrderException(OrderErrorCode.ORDER_ACCESS_DENIED);
    }
}
