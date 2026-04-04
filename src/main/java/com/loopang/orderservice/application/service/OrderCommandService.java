package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderCreateCommandDto;
import com.loopang.orderservice.application.dto.OrderCreateResultDto;
import com.loopang.orderservice.application.dto.OrderDeleteCommandDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.repository.OrderRepository;
import com.loopang.orderservice.domain.service.*;
import com.loopang.orderservice.domain.service.dto.CompanyData;
import com.loopang.orderservice.domain.service.dto.HubData;
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
	private final OrderValidator orderValidator;
	private final OrderDtoMapper orderDtoMapper;
	private final OrderAccess orderAccess;

	private final UserProvider userProvider;
	private final CompanyProvider companyProvider;
	private final ItemProvider itemProvider;
	private final HubProvider hubProvider;

	// TODO: 주문 생성 직후 주문 -> 허브 방향 이벤트 발송
	@Transactional(propagation = Propagation.REQUIRED)
	public OrderCreateResultDto createOrder(OrderCreateCommandDto request, UserType userType) {
		// 1. 주문 생성 권한 검증
		orderAccess.validateCreateAccess(userType);

		// 2. 주문상품, 공급업체, 수령업체 조회
		ItemData itemData = itemProvider.getItem(request.getItemId());
		CompanyData supplierData = companyProvider.getCompany(request.getSupplierId());
		CompanyData receiverData = companyProvider.getCompany(request.getReceiverId());
		orderValidator.validateCompanyAndItem(supplierData, receiverData, itemData);

		// 3. 허브 정보 조회
		HubData supplierHub = hubProvider.getHub(supplierData.getHubId());
		HubData receiverHub = hubProvider.getHub(receiverData.getHubId());

		// 5. 공급업체, 수령업체, 주문상품 VO 구성 및 엔터티 생성
		Supplier supplier = Supplier.of(supplierData, supplierHub);
		Receiver receiver = Receiver.of(receiverData, receiverHub, request.getRequirements());
		OrderItem orderItem = OrderItem.of(itemData, request.getQuantity(), 1);

		Order order = Order.create(supplier, receiver, orderItem);
		Order savedOrder = orderRepository.save(order);

		return orderDtoMapper.toCreateResultDto(savedOrder);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public OrderDeleteCommandDto deleteOrder(UUID orderId, UUID userId, UserType userType) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		UUID managedHubId = userProvider.getHubIdIfHubManager(userId, userType);
		orderAccess.validateUpdateDeleteAccess(userId, userType, managedHubId, order);

		order.delete(userId);

		return OrderDeleteCommandDto.from(order);
	}
}
