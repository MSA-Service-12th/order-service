package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.service.dto.CourierData;
import com.loopang.orderservice.domain.vo.UserType;

import java.util.UUID;

public interface DeliveryProvider {

	CourierData getCourierId(Order order);

	default UUID getAssignedCourier(Order order, UUID userId, UserType userType) {
		CourierData assigned = this.getCourierId(order);
		if (userType != UserType.DELIVERY || assigned == null) {
			return null;
		}
		if (assigned.companyCourierId() != null && assigned.companyCourierId().equals(userId)) {
			return assigned.companyCourierId();
		}
		if (assigned.hubCourierId() != null && assigned.hubCourierId().equals(userId)) {
			return assigned.hubCourierId();
		}
		return null;
	}
}
