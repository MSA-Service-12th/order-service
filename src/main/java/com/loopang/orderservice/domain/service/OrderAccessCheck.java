package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.entity.Order;

import java.util.UUID;

public interface OrderAccessCheck {

	// 허브관리자의 경우, 담당허브ID 필요 -> 주문상태가 PENDING일 경우
	void checkUpdateAccess(Order order, UUID userId, String role, UUID hubId);
	void checkDeleteAccess(Order order, UUID userId, String role, UUID hubId);
}
