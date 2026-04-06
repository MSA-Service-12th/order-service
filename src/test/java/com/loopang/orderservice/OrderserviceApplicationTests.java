package com.loopang.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "order-pending-topic",
        "order-accepted-topic",
        "hub-update-topic",
        "delivery-create-topic",
        "delivery-update-topic"
})
class OrderserviceApplicationTests {

	@Test
	void contextLoads() {
	}

}
