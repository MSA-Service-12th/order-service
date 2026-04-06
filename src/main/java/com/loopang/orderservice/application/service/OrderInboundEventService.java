package com.loopang.orderservice.application.service;

import com.loopang.orderservice.domain.event.payload.DeliveryUpdatePayload;
import com.loopang.orderservice.domain.event.payload.HubUpdatePayload;

public interface OrderInboundEventService {
	void handleInventoryResult(HubUpdatePayload payload);
	void handleInventoryCheckFailure(HubUpdatePayload payload);
	void handleDeliveryCreation(DeliveryUpdatePayload payload);
	void handleDeliveryCompletion(DeliveryUpdatePayload payload);
	void handleDeliveryRollback(DeliveryUpdatePayload payload, boolean isForce);
	void handleDeliveryStatusUpdate(DeliveryUpdatePayload payload);
}
