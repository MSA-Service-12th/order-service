package com.loopang.orderservice.infrastructure.kafka;

import com.loopang.common.domain.outbox.Outbox;
import com.loopang.common.domain.outbox.OutboxRepository;
import com.loopang.common.domain.outbox.OutboxStatus;
import com.loopang.common.event.OutboxEvent;
import com.loopang.common.util.JsonUtil;
import com.loopang.orderservice.application.dto.OrderCreateCommandDto;
import com.loopang.orderservice.application.service.OrderCommandFacade;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.event.payload.HubUpdatePayload;
import com.loopang.orderservice.domain.repository.OrderRepository;
import com.loopang.orderservice.domain.service.CompanyProvider;
import com.loopang.orderservice.domain.service.HubProvider;
import com.loopang.orderservice.domain.service.ItemProvider;
import com.loopang.orderservice.domain.service.dto.*;
import com.loopang.orderservice.domain.vo.*;
import com.loopang.orderservice.presentation.dto.OrderCreateRequestDto;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

// Eureka Server, Config Server 실행 중인 상태일 때 정상적으로 테스트 가능
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"order-pending-topic", "hub-update-topic"})
public class OrderMessagingIntegrationTest {

    @Autowired
    private OrderCommandFacade orderCommandFacade;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private JsonUtil jsonUtil;

    @MockitoBean private CompanyProvider companyProvider;
    @MockitoBean private ItemProvider itemProvider;
    @MockitoBean private HubProvider hubProvider;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new StringDeserializer()).createConsumer();
        consumer.subscribe(Collections.singleton("order-pending-topic"));
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @DisplayName("주문 생성 시 Outbox에 PENDING 이벤트가 저장되어야 한다")
    void shouldSavePendingEventToOutbox() {
        // given
        UUID supplierId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        
        given(itemProvider.getItem(any())).willReturn(new ItemData(itemId, "Test Item", supplierId));
        given(companyProvider.getCompany(supplierId)).willReturn(new CompanyData(supplierId, "Supplier", CompanyType.SUPPLIER, new HubInfoData(UUID.randomUUID(), "S-Hub"), "Addr"));
        given(companyProvider.getCompany(receiverId)).willReturn(new CompanyData(receiverId, "Receiver", CompanyType.RECEIVER, new HubInfoData(UUID.randomUUID(), "R-Hub"), "Addr"));
        given(hubProvider.getHub(any())).willReturn(new HubData(UUID.randomUUID(), "Hub", new HubAddressData("FullAddr")));

        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(supplierId, receiverId, itemId, 5, "Req");
        OrderCreateCommandDto request = OrderCreateCommandDto.from(requestDto);

        // when
        orderCommandFacade.createOrder(request, "test-slack-id", UserType.COMPANY);

        // then: Outbox에 데이터가 생성되었는지 확인
        List<Outbox> outboxes = outboxRepository.findAll();
        assertThat(outboxes).isNotEmpty();
        
        Outbox pendingEvent = outboxes.stream()
                .filter(o -> o.getEventType().contains("pending"))
                .findFirst()
                .orElseThrow();
        
        assertThat(pendingEvent.getDomainType()).isEqualTo("ORDER");
        assertThat(pendingEvent.getStatus()).isIn(OutboxStatus.PENDING, OutboxStatus.PROCESSED);
    }

    @Test
    @DisplayName("Kafka를 통해 허브 재고 확인 결과를 수신하면 주문 상태가 변경되어야 한다")
    void shouldUpdateStatusWhenReceivingHubUpdateFromKafka() throws InterruptedException {
        // given: 필수 필드를 채운 주문 생성
        Supplier supplier = Supplier.of(
                new CompanyData(UUID.randomUUID(), "S", CompanyType.SUPPLIER, new HubInfoData(UUID.randomUUID(), "SH"), "Addr"),
                new HubData(UUID.randomUUID(), "SH", new HubAddressData("Addr"))
        );
        Receiver receiver = Receiver.of(
                new CompanyData(UUID.randomUUID(), "R", CompanyType.RECEIVER, new HubInfoData(UUID.randomUUID(), "RH"), "Addr"),
                new HubData(UUID.randomUUID(), "RH", new HubAddressData("Addr")),
                "Req",
                "test-slack-id"
        );
        OrderItem item = OrderItem.of(new ItemData(UUID.randomUUID(), "Item", UUID.randomUUID()), 10, 1);
        
        Order order = Order.create(supplier, receiver, item);
        Order savedOrder = orderRepository.save(order);
        UUID orderId = savedOrder.getOrderId();

        HubUpdatePayload payload = new HubUpdatePayload(orderId, UUID.randomUUID(), UUID.randomUUID(), 10, 5);
        OutboxEvent outboxEvent = new OutboxEvent(UUID.randomUUID().toString(), "ORDER", orderId, "hub-update-topic", payload);
        String message = jsonUtil.toJson(outboxEvent);

        // when: Kafka로 메시지 직접 전송
        kafkaTemplate.send("hub-update-topic", orderId.toString(), message);

        // then: 리스너가 비동기로 처리할 시간 확보
        int attempts = 0;
        Order updatedOrder = null;
        while (attempts < 20) {
            updatedOrder = orderRepository.findById(orderId).orElseThrow();
            if (updatedOrder.getStatus() == OrderStatus.WAIT_TO_APPROVAL) {
                break;
            }
            Thread.sleep(500);
            attempts++;
        }

        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.WAIT_TO_APPROVAL);
    }
}
