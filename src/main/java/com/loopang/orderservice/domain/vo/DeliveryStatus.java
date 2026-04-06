package com.loopang.orderservice.domain.vo;

public enum DeliveryStatus {
	START_DELIVERY,
	COMPLETED,
	CANCELLED;

	public static DeliveryStatus of(String status) {
		if (status == null) return null;
		return switch (status.toUpperCase()) {
			case "HUB_WAITING", "START_DELIVERY" -> START_DELIVERY;
			case "DELIVERED", "COMPLETED" -> COMPLETED;
			case "CANCELLED", "FAILED" -> CANCELLED;
			default -> throw new IllegalArgumentException("지원하지 않는 배송 상태입니다: " + status);
		};
	}
}
