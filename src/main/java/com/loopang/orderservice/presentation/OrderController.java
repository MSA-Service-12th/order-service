package com.loopang.orderservice.presentation;

import com.loopang.orderservice.application.dto.OrderCreateCommandDto;
import com.loopang.orderservice.application.dto.OrderDeleteCommandDto;
import com.loopang.orderservice.application.dto.OrderDetailsDto;
import com.loopang.orderservice.application.dto.OrderSearchConditionDto;
import com.loopang.orderservice.application.service.OrderCommandService;
import com.loopang.orderservice.application.service.OrderQueryService;
import com.loopang.orderservice.domain.vo.UserType;
import com.loopang.orderservice.presentation.dto.OrderCreateRequestDto;
import com.loopang.orderservice.presentation.dto.OrderDeleteResponseDto;
import com.loopang.orderservice.presentation.dto.OrderListResponseDto;
import com.loopang.orderservice.presentation.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
	public OrderResponseDto createOrder(@RequestBody OrderCreateRequestDto requestDto) {
		OrderDetailsDto details = orderCommandService.createOrder(OrderCreateCommandDto.from(requestDto));
		return OrderResponseDto.from(details);
	}

	@GetMapping("/{orderId}")
	public OrderResponseDto getOrder(
			@PathVariable UUID orderId,
			@RequestHeader(value = "X-User-Id") UUID userId,
			@RequestHeader(value = "X-User-Role") String userRole) {
		OrderDetailsDto details = orderQueryService.getOrder(orderId, userId, UserType.from(userRole));
		return OrderResponseDto.from(details);
	}

	@GetMapping
	public Page<OrderListResponseDto> searchOrders(
			@ModelAttribute OrderSearchConditionDto condition,
			@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestHeader(value = "X-User-Id") UUID userId,
			@RequestHeader(value = "X-User-Role") String userRole) {

		int size = pageable.getPageSize();
		if (size != 10 && size != 30 && size != 50) {
			size = 10;
		}

		Pageable adjustedPageable = PageRequest.of(
				pageable.getPageNumber(),
				size,
				pageable.getSort()
		);

		return orderQueryService.searchOrders(condition, adjustedPageable, userId, UserType.from(userRole))
				.map(OrderListResponseDto::from);
	}

	@DeleteMapping("/{orderId}")
	public OrderDeleteResponseDto deleteOrder(
			@PathVariable UUID orderId,
			@RequestHeader(value = "X-User-Id") UUID userId,
			@RequestHeader(value = "X-User-Role") String userRole) {
		OrderDeleteCommandDto result
				= orderCommandService.deleteOrder(OrderDeleteCommandDto.of(orderId, userId, UserType.from(userRole)));
		return OrderDeleteResponseDto.from(result);
	}
}
