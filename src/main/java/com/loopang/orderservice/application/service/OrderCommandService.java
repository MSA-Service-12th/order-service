package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderCreateCommandDto;
import com.loopang.orderservice.application.dto.OrderDeleteCommandDto;
import com.loopang.orderservice.application.dto.OrderDetailsDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.repository.OrderRepository;
import com.loopang.orderservice.domain.service.*;
import com.loopang.orderservice.domain.service.dto.ItemData;
import com.loopang.orderservice.domain.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandService {

	private final OrderRepository orderRepository;
	private final OrderDtoMapper orderDtoMapper;

	private final OrderValidator orderValidator;

	private final CompanyProvider companyProvider;
	private final ItemProvider itemProvider;
	private final HubProvider hubProvider;
	private final UserProvider userProvider;

	@Transactional(propagation = Propagation.REQUIRED)
	public OrderDetailsDto createOrder(OrderCreateCommandDto request) {

		Supplier supplier = companyProvider.getSupplier(request.getSupplierId(), request.getRequirements());
		Receiver receiver = companyProvider.getReceiver(request.getReceiverId(), userProvider);
		ItemData itemData = itemProvider.getItem(request.getItemId());

		OrderItemInfo itemInfo = OrderItemInfo.from(itemData);
		orderValidator.validate(supplier, receiver, itemInfo);

		HubInfo supplierHub = hubProvider.getHub(supplier.getHubId());
		HubInfo receiverHub = hubProvider.getHub(receiver.getHubId());

		supplier.updateHubInfo(supplierHub);
		receiver.updateHubInfo(receiverHub);

		OrderItem orderItem = OrderItem.of(itemInfo, request.getQuantity(), 1);

		Order order = Order.create(supplier, receiver, orderItem);
		Order savedOrder = orderRepository.save(order);

		return orderDtoMapper.toDetailsDto(savedOrder);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public OrderDeleteCommandDto deleteOrder(UUID orderId, UUID userId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		order.delete(userId);

		return OrderDeleteCommandDto.from(order);
	}
}
