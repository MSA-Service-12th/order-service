package com.loopang.orderservice.presentation;

import com.loopang.orderservice.application.dto.*;
import com.loopang.orderservice.application.service.OrderCommandFacade;
import com.loopang.orderservice.application.service.OrderQueryFacade;
import com.loopang.orderservice.application.service.OrderQueryService;
import com.loopang.orderservice.domain.vo.UserType;
import com.loopang.orderservice.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// TODO: Security 도입 후 요청 헤더를 사용한 부분을 @AuthenticationPrinciple로 대체
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderCommandFacade orderCommandFacade;
	private final OrderQueryFacade orderQueryFacade;

	@PostMapping
	public OrderCreateResponseDto createOrder(
			@Valid @RequestBody OrderCreateRequestDto requestDto,
			@RequestHeader(value = "X-User-Role") String userRole) {
		OrderCreateResultDto result
				= orderCommandFacade.createOrder(OrderCreateCommandDto.from(requestDto), UserType.from(userRole));

		return OrderCreateResponseDto.from(result);
	}

	@GetMapping("/{orderId}")
	public OrderDetailResponseDto getOrder(
			@PathVariable UUID orderId,
			@RequestHeader(value = "X-User-UUID") UUID userId,
			@RequestHeader(value = "X-User-Role") String userRole) {
		OrderDetailsDto details = orderQueryFacade.getOrder(orderId, userId, UserType.from(userRole));

		return OrderDetailResponseDto.from(details);
	}

	@GetMapping
	public Page<OrderSummaryResponseDto> searchOrders(
			@ModelAttribute OrderSearchConditionDto condition,
			@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestHeader(value = "X-User-UUID") UUID userId,
			@RequestHeader(value = "X-User-Role") String userRole) {

		return orderQueryFacade.searchOrders(condition, pageable, userId, UserType.from(userRole))
				.map(OrderSummaryResponseDto::from);
	}

	@DeleteMapping("/{orderId}")
	public OrderDeleteResponseDto deleteOrder(
			@PathVariable UUID orderId,
			@RequestHeader(value = "X-User-UUID") UUID userId,
			@RequestHeader(value = "X-User-Role") String userRole) {
		OrderDeleteCommandDto result = orderCommandFacade.deleteOrder(orderId, userId, UserType.from(userRole));

		return OrderDeleteResponseDto.from(result);
	}

	@PatchMapping("/{orderId}/approve")
	public void approveOrder(
			@PathVariable UUID orderId,
			@RequestHeader(value = "X-User-UUID") UUID userId,
			@RequestHeader(value = "X-User-Role") String userRole) {
		orderCommandFacade.approveOrder(orderId, userId, UserType.from(userRole));
	}

	@PatchMapping("/{orderId}/cancel")
	public void cancelOrder(
			@PathVariable UUID orderId,
			@RequestHeader(value = "X-User-UUID") UUID userId,
			@RequestHeader(value = "X-User-Role") String userRole) {
		orderCommandFacade.cancelOrder(orderId, userId, UserType.from(userRole));
	}
}
