package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderCreateCommandDto;
import com.loopang.orderservice.application.dto.OrderCreateResultDto;
import com.loopang.orderservice.application.dto.OrderDeleteCommandDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.event.OrderEvents;
import com.loopang.orderservice.domain.service.*;
import com.loopang.orderservice.domain.service.dto.CompanyData;
import com.loopang.orderservice.domain.service.dto.HubData;
import com.loopang.orderservice.domain.service.dto.ItemData;
import com.loopang.orderservice.domain.service.dto.UserData;
import com.loopang.orderservice.domain.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCommandFacade implements OrderCommandService {

	private final OrderValidator orderValidator;
	private final OrderDtoMapper orderDtoMapper;
	private final OrderAccess orderAccess;

	private final CompanyProvider companyProvider;
	private final ItemProvider itemProvider;
	private final HubProvider hubProvider;
	private final UserProvider userProvider;
	private final OrderCommandCore orderCommandCore;
	private final OrderEvents orderEvents;

	@Override
	public OrderCreateResultDto createOrder(OrderCreateCommandDto request, String slackId, UserType userType) {
		// 1. 주문 생성 권한 검증
		orderAccess.validateCreateAccess(userType);

		// 2. 원격 조회 및 검증 (트랜잭션 외부)
		ItemData itemData = itemProvider.getItem(request.getItemId());
		CompanyData supplierData = companyProvider.getCompany(request.getSupplierId());
		CompanyData receiverData = companyProvider.getCompany(request.getReceiverId());
		orderValidator.validateCompanyAndItem(supplierData, receiverData, itemData);

		HubData supplierHub = hubProvider.getHub(supplierData.getHubId());
		HubData receiverHub = hubProvider.getHub(receiverData.getHubId());

		// 3. VO 구성
		Supplier supplier = Supplier.of(supplierData, supplierHub);
		Receiver receiver = Receiver.of(receiverData, receiverHub, request.getRequirements(), slackId);
		OrderItem orderItem = OrderItem.of(itemData, request.getQuantity(), 1);

		// 4. 핵심 로직 호출 (트랜잭션 진입)
		Order savedOrder = orderCommandCore.saveOrder(supplier, receiver, orderItem);

		// 5. 주문 -> 허브 방향 이벤트 발행 (재고 확인 요청)
		orderEvents.pending(savedOrder);

		return orderDtoMapper.toCreateResultDto(savedOrder);
	}

	@Override
	public OrderDeleteCommandDto deleteOrder(UUID orderId, UUID userId, UserType userType) {
		// 원격 조회 (트랜잭션 외부)
		UUID managedHubId = userProvider.getHubIdIfHubManager(userId, userType);

		// 핵심 로직 호출 (트랜잭션 진입)
		Order order = orderCommandCore.deleteOrder(orderId, userId, managedHubId, userType);

		return OrderDeleteCommandDto.from(order);
	}

	@Override
	public void approveOrder(UUID orderId, UUID userId, UserType userType) {
		// 원격 조회 (트랜잭션 외부)
		UserData user = userProvider.getUser(userId);
		UUID managedHubId = (userType == UserType.HUB) ? user.hubId() : null;

		// 핵심 로직 호출 (트랜잭션 진입)
		Order order = orderCommandCore.approveOrder(orderId, userId, user.name(), managedHubId, userType);

		// 주문 승인됨 이벤트 발행 (배송 서비스에서 수신)
		orderEvents.accepted(order);
	}

	@Override
	public void cancelOrder(UUID orderId, UUID userId, UserType userType) {
		// 원격 조회 (트랜잭션 외부)
		UserData user = userProvider.getUser(userId);
		UUID managedHubId = (userType == UserType.HUB) ? user.hubId() : null;

		// 핵심 로직 호출 (트랜잭션 진입)
		Order order = orderCommandCore.cancelOrder(orderId, userId, managedHubId, userType);

		// 주문 취소됨 이벤트 발행 (허브 서비스에서 수신하여 재고 복원)
		orderEvents.cancelled(order);
	}
}
