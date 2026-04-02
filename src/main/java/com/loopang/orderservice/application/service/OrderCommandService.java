package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderCreateInputDto;
import com.loopang.orderservice.application.dto.OrderCreateOutputDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.repository.OrderRepository;
import com.loopang.orderservice.domain.service.*;
import com.loopang.orderservice.domain.service.dto.ItemData;
import com.loopang.orderservice.domain.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 이 클래스에서는 주문 생성, 수정, 삭제에 관한 실제 서비스 로직을 구현(이벤트 처리는 별도의 클래스에서 수행할 예정)
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandService {

	private final OrderRepository orderRepository;

	private final OrderAccess orderAccess;
	private final OrderValidator orderValidator;

	private final CompanyProvider companyProvider;
	private final ItemProvider itemProvider;
	private final HubProvider hubProvider;
	private final UserProvider userProvider;

	/**
	 * 주문 생성 (주문 프로세스 전반부)
	 * 1. 업체 및 상품 유효성 확인 (존재 여부)
	 * 2. 업체 타입 검증 (SUPPLIER/RECEIVER)
	 * 3. 허브 정보 조회 및 보완 (출발지/도착지 허브명, 주소)
	 * 4. 주문 엔티티 생성 및 저장
	 */
	@Transactional
	public OrderCreateOutputDto createOrder(OrderCreateInputDto request) {

		// 1. 공급업체, 수령업체, 상품 조회 및 null 체크
		Supplier supplier = companyProvider.getSupplier(request.getSupplierId(), request.getRequirements());
		Receiver receiver = companyProvider.getReceiver(request.getReceiverId(), userProvider);
		ItemData itemData = itemProvider.getItem(request.getItemId());

		OrderItemInfo itemInfo = OrderItemInfo.from(itemData);
		orderValidator.validate(supplier, receiver, itemInfo);

		// 2. 업체는 반드시 특정 허브에 소속되므로 허브ID를 통해 상세 정보(이름, 주소)를 조회함
		HubInfo supplierHub = hubProvider.getHub(supplier.getHubId());
		HubInfo receiverHub = hubProvider.getHub(receiver.getHubId());

		supplier.updateHubInfo(supplierHub);
		receiver.updateHubInfo(receiverHub);

		// 3. 주문 항목 구성 (기본 상품순번 1 적용)
		OrderItem orderItem = OrderItem.of(itemInfo, request.getQuantity(), 1);

		// 4. 주문 엔티티 생성 및 저장 (상태는 PENDING으로 시작)
		Order order = Order.create(supplier, receiver, orderItem);
		Order savedOrder = orderRepository.save(order);

		// TODO: 주문 생성 이벤트 발송 (주문 -> 허브 방향: 허브 재고 차감 요청)
		// publishOrderCreatedEvent(savedOrder);

		return OrderCreateOutputDto.from(savedOrder);
	}
}
