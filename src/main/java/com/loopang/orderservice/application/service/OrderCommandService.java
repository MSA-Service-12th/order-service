package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderCreateCommandDto;
import com.loopang.orderservice.application.dto.OrderCreateResultDto;
import com.loopang.orderservice.application.dto.OrderDeleteCommandDto;
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
	private final OrderValidator orderValidator;
	private final OrderDtoMapper orderDtoMapper;
	private final OrderAccess orderAccess;

	private final UserProvider userProvider;
	private final CompanyProvider companyProvider;
	private final ItemProvider itemProvider;
	private final HubProvider hubProvider;

	@Transactional(propagation = Propagation.REQUIRED)
	public OrderCreateResultDto createOrder(OrderCreateCommandDto request, UserType userType) {
		// 1. 주문 생성 권한 검증
		orderAccess.validateCreateAccess(userType);

		// 2. 공급업체, 수령업체, 주문상품이 실재하는지 확인
		Supplier supplier = companyProvider.getSupplier(request.getSupplierId(), request.getRequirements());
		Receiver receiver = companyProvider.getReceiver(request.getReceiverId(), userProvider);
		ItemData itemData = itemProvider.getItem(request.getItemId());

		// 3. 데이터 무결성 검증 (업체-상품 일치 여부, 허브 존재 여부 등)
		orderValidator.validateCreateOrder(supplier, receiver, itemData);

		// 4. 조회한 허브 정보를 바탕으로 공급업체/수령업체의 허브 정보 업데이트
		HubInfo supplierHub = hubProvider.getHub(supplier.getHubId());
		HubInfo receiverHub = hubProvider.getHub(receiver.getHubId());
		supplier.updateHubInfo(supplierHub);
		receiver.updateHubInfo(receiverHub);

		// 5. 주문 엔티티 생성 및 저장
		OrderItemInfo itemInfo = OrderItemInfo.from(itemData);
		OrderItem orderItem = OrderItem.of(itemInfo, request.getQuantity(), 1);
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
