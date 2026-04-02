package com.loopang.orderservice.domain.vo;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;

public enum CompanyType {

	SUPPLIER,
	RECEIVER

	;

	public static CompanyType from(String companyType) {
		try {
			return CompanyType.valueOf(companyType.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_COMPANY_TYPE);
		}
	}
}
