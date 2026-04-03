package com.loopang.orderservice.presentation;

import com.loopang.orderservice.application.dto.*;
import com.loopang.orderservice.application.service.OrderCommandService;
import com.loopang.orderservice.application.service.OrderQueryService;
import com.loopang.orderservice.domain.vo.UserType;
import com.loopang.orderservice.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderCommandService orderCommandService;
	private final OrderQueryService orderQueryService;

	@PostMapping
	public OrderCreateResponseDto createOrder(@RequestBody OrderCreateRequestDto requestDto) {
		OrderCreateResultDto result = orderCommandService.createOrder(OrderCreateCommandDto.from(requestDto));

		return OrderCreateResponseDto.from(result);
	}

	@GetMapping("/{orderId}")
	public OrderDetailResponseDto getOrder(
			@PathVariable UUID orderId,
			@RequestHeader(value = "X-User-Id") UUID userId,
			@RequestHeader(value = "X-User-Role") String userRole) {
		OrderDetailsDto details = orderQueryService.getOrder(orderId, userId, UserType.from(userRole));

		return OrderDetailResponseDto.from(details);
	}

	@GetMapping
	public Page<OrderSummaryResponseDto> searchOrders(
			@ModelAttribute OrderSearchConditionDto condition,
			@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestHeader(value = "X-User-Id") UUID userId,
			@RequestHeader(value = "X-User-Role") String userRole) {

		return orderQueryService.searchOrders(condition, pageable, userId, UserType.from(userRole))
				.map(OrderSummaryResponseDto::from);
	}

	@DeleteMapping("/{orderId}")
	public OrderDeleteResponseDto deleteOrder(
			@PathVariable UUID orderId,
			@RequestHeader(value = "X-User-Id") UUID userId,
			@RequestHeader(value = "X-User-Role") String userRole) {
		OrderDeleteCommandDto result = orderCommandService.deleteOrder(orderId, userId);

		return OrderDeleteResponseDto.from(result);
	}
}
