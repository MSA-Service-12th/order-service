package com.loopang.orderservice.domain.vo;

public enum OrderStatus {

	PENDING,
	WAIT_TO_APPROVAL,
	ACCEPTED,
	ON_DELIVERY,
	COMPLETED,
	CANCELLED

	;

	public boolean checkTransition(OrderStatus targetStatus) {
		return switch (this) {
			case PENDING
					-> targetStatus == WAIT_TO_APPROVAL || targetStatus == CANCELLED;
			case WAIT_TO_APPROVAL
					-> targetStatus == ACCEPTED || targetStatus == CANCELLED;
			case ACCEPTED
					-> targetStatus == ON_DELIVERY || targetStatus == CANCELLED;
			case ON_DELIVERY
					-> targetStatus == COMPLETED || targetStatus == CANCELLED;
			case CANCELLED, COMPLETED -> false;
		};
	}
}
