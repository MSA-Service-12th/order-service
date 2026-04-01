package com.loopang.orderservice.domain.exception;

import com.loopang.common.exception.CustomException;
import com.loopang.common.exception.ErrorCodeSpec;
import org.springframework.http.HttpStatus;

public class OrderException extends CustomException {

	public OrderException(ErrorCodeSpec errorCode) {
		super(errorCode);
	}

	public OrderException(HttpStatus status, String message, String field) {
		super(status, message, field);
	}
}
