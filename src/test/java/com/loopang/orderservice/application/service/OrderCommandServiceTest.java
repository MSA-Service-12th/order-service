package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderCreateCommandDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.event.OrderEvents;
import com.loopang.orderservice.domain.event.payload.HubUpdatePayload;
import com.loopang.orderservice.domain.repository.OrderRepository;
import com.loopang.orderservice.domain.service.*;
import com.loopang.orderservice.domain.service.dto.*;
import com.loopang.orderservice.domain.vo.CompanyType;
import com.loopang.orderservice.domain.vo.OrderStatus;
import com.loopang.orderservice.domain.vo.UserType;
import com.loopang.orderservice.presentation.dto.OrderCreateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderAccess orderAccess;
    @Mock private OrderEvents orderEvents;
    @Mock private CompanyProvider companyProvider;
    @Mock private ItemProvider itemProvider;
    @Mock private HubProvider hubProvider;
    @Mock private UserProvider userProvider;
    @Mock private OrderValidator orderValidator;
    @Mock private OrderDtoMapper orderDtoMapper;

    private OrderCommandCore orderCommandCore;
    private OrderInboundEventServiceImpl orderInboundEventService;
    private OrderCommandFacade orderCommandFacade;

    @BeforeEach
    void setUp() {
        orderCommandCore = new OrderCommandCore(orderRepository, orderAccess);
        orderInboundEventService = new OrderInboundEventServiceImpl(orderCommandCore, orderEvents);
        orderCommandFacade = new OrderCommandFacade(
                orderValidator, orderDtoMapper, orderAccess,
                companyProvider, itemProvider, hubProvider, userProvider,
                orderCommandCore, orderEvents
        );
    }

    @Test
    @DisplayName("주문 생성 시 PENDING 상태로 저장되고 이벤트가 발행되어야 한다")
    void createOrder_Success() {
        // given
        UUID itemId = UUID.randomUUID();
        UUID supplierId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        
        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(supplierId, receiverId, itemId, 10, "Requirements");
        OrderCreateCommandDto request = OrderCreateCommandDto.from(requestDto);
        
        CompanyData supplierData = new CompanyData(supplierId, "Supplier", CompanyType.SUPPLIER, UUID.randomUUID(), "HubName", "Address");
        CompanyData receiverData = new CompanyData(receiverId, "Receiver", CompanyType.RECEIVER, UUID.randomUUID(), "HubName", "Address");
        HubData hubData = new HubData(UUID.randomUUID(), "Hub", new HubAddressData("FullAddress"));

        given(itemProvider.getItem(any())).willReturn(new ItemData(itemId, "Item", supplierId));
        given(companyProvider.getCompany(supplierId)).willReturn(supplierData);
        given(companyProvider.getCompany(receiverId)).willReturn(receiverData);
        given(hubProvider.getHub(any())).willReturn(hubData);
        
        Order mockOrder = mock(Order.class);
        given(orderRepository.save(any(Order.class))).willReturn(mockOrder);

        // when
        orderCommandFacade.createOrder(request, "test-slack-id", UserType.COMPANY);

        // then
        verify(orderRepository).save(any(Order.class));
        verify(orderEvents).pending(mockOrder);
    }

    @Test
    @DisplayName("허브 재고 확인 결과가 성공(balance >= 0)이면 상태가 WAIT_TO_APPROVAL로 전이되어야 한다")
    void handleInventoryResult_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        HubUpdatePayload payload = new HubUpdatePayload(orderId, UUID.randomUUID(), UUID.randomUUID(), 10, 5);
        
        Order order = Order.create(null, null, null); // status is PENDING
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        orderInboundEventService.handleInventoryResult(payload);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAIT_TO_APPROVAL);
    }

    @Test
    @DisplayName("허브 재고 확인 결과가 실패(balance < 0)이면 상태가 CANCELLED로 전이되어야 한다")
    void handleInventoryResult_Failure() {
        // given
        UUID orderId = UUID.randomUUID();
        HubUpdatePayload payload = new HubUpdatePayload(orderId, UUID.randomUUID(), UUID.randomUUID(), 10, -1);
        
        Order order = Order.create(null, null, null);
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        orderInboundEventService.handleInventoryResult(payload);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("허브관리자가 주문을 승인하면 상태가 ACCEPTED로 바뀌고 이벤트가 발행되어야 한다")
    void approveOrder_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        Order order = Order.create(null, null, null);
        order.waitToApproval(); // PENDING -> WAIT_TO_APPROVAL
        
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(userProvider.getUser(userId)).willReturn(new UserData(UUID.randomUUID(), "ManagerName", "SlackId"));

        // when
        orderCommandFacade.approveOrder(orderId, userId, UserType.HUB);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        verify(orderEvents).accepted(order);
    }

    @Test
    @DisplayName("허브관리자가 주문을 취소하면 상태가 CANCELLED로 바뀌고 이벤트가 발행되어야 한다")
    void cancelOrder_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        Order order = Order.create(null, null, null); // PENDING
        
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(userProvider.getUser(userId)).willReturn(new UserData(UUID.randomUUID(), "ManagerName", "SlackId"));

        // when
        orderCommandFacade.cancelOrder(orderId, userId, UserType.HUB);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderEvents).cancelled(order);
    }
}
