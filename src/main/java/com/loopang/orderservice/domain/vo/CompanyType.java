package com.loopang.orderservice.domain.vo;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;

import java.util.Arrays;

public enum CompanyType {

	SUPPLIER,
	RECEIVER

	;

	public static CompanyType find(String companyType) {
		return Arrays.stream(CompanyType.values())
				.filter(type -> type.name().equals(companyType))
				.findFirst()
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_INVALID_COMPANY_TYPE));
	}
}
