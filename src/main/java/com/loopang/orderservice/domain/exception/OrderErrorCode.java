package com.loopang.orderservice.domain.exception;

import com.loopang.common.exception.ErrorCodeSpec;
import org.springframework.http.HttpStatus;

public enum OrderErrorCode implements ErrorCodeSpec {

	ORDER_NOT_FOUND("ORDER_001", HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),
	ORDER_INSUFFICIENT_STOCK("ORDER_002", HttpStatus.BAD_REQUEST, "주문한 상품의 재고가 부족합니다."),
	ORDER_ACCESS_DENIED("ORDER_OO3", HttpStatus.FORBIDDEN, "해당 주문에 관한 권한이 없습니다."),
	ORDER_INVALID_STATUS_TRANSITION("ORDER_004", HttpStatus.BAD_REQUEST, "주문상태를 변경할 수 없습니다.")

	;

	private final String code;
	private final HttpStatus status;
	private final String message;

	OrderErrorCode(String code, HttpStatus status, String message) {
		this.code = code;
		this.status = status;
		this.message = message;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getField() {
		return "order";
	}
}
