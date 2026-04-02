package com.loopang.orderservice.domain.vo;

public enum UserType {

	MASTER,
	HUB,
	DELIVERY,
	COMPANY,

	;

	public String toRole() {
		return "ROLE_" + this.name();
	}
}
