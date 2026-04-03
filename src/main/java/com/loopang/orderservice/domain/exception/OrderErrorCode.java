package com.loopang.orderservice.domain.exception;

import com.loopang.common.exception.ErrorCodeSpec;
import org.springframework.http.HttpStatus;

public enum OrderErrorCode implements ErrorCodeSpec {

	ORDER_NOT_FOUND("ORDER_001", HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),
	ORDER_INSUFFICIENT_STOCK("ORDER_002", HttpStatus.BAD_REQUEST, "주문한 상품의 재고가 부족합니다."),
	ORDER_ACCESS_DENIED("ORDER_003", HttpStatus.FORBIDDEN, "해당 주문에 관한 권한이 없습니다."),
	ORDER_INVALID_STATUS_TRANSITION("ORDER_004", HttpStatus.BAD_REQUEST, "주문상태를 변경할 수 없습니다."),
	ORDER_CANNOT_DELETE("ORDER_005", HttpStatus.BAD_REQUEST, "주문 삭제를 진행할 수 없는 상태입니다."),
	ORDER_INVALID_SUPPLIER("ORDER_006", HttpStatus.BAD_REQUEST, "현재 업체가 유효한 공급업체가 아닙니다."),
	ORDER_INVALID_RECEIVER("ORDER_007", HttpStatus.BAD_REQUEST, "현재 업체가 유효한 수령업체가 아닙니다."),
	ORDER_INVALID_COMPANY_TYPE("ORDER_008", HttpStatus.BAD_REQUEST, "현재 업체의 업체타입이 유효하지 않습니다."),
	ORDER_INVALID_QUANTITY("ORDER_009", HttpStatus.BAD_REQUEST, "유효하지 않은 주문수량입니다."),
	ORDER_HUB_NOT_FOUND("ORDER_010", HttpStatus.BAD_REQUEST, "업체의 담당 허브 정보를 찾을 수 없습니다."),
	ORDER_ITEM_NOT_FOUND("ORDER_011", HttpStatus.NOT_FOUND, "주문할 상품을 찾을 수 없습니다."),
	ORDER_FORBIDDEN("ORDER_012", HttpStatus.FORBIDDEN, "해당 작업을 수행할 권한이 없습니다."),
	ORDER_INVALID_ITEM("ORDER_013", HttpStatus.BAD_REQUEST, "주문할 상품을 제공하지 않는 업체입니다.")

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
