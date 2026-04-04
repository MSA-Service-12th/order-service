package com.loopang.orderservice.infrastructure.persistence;

import static com.loopang.orderservice.domain.entity.QOrder.order;
import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;

import com.loopang.orderservice.application.dto.OrderSearchConditionDto;
import com.loopang.orderservice.domain.vo.OrderStatus;
import com.loopang.orderservice.domain.vo.UserType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.UUID;

public class OrderQueryCondition {

	public static BooleanBuilder createSearchCondition(OrderSearchConditionDto condition) {
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (condition == null) {
			return booleanBuilder;
		}

		return booleanBuilder
				.and(keywordContains(condition.keyword()))
				.and(supplierNameContains(condition.supplierName()))
				.and(receiverNameContains(condition.receiverName()))
				.and(itemNameContains(condition.itemName()))
				.and(statusEq(condition.status()));
	}

	public static void applyAuthorizationFilter(BooleanBuilder builder, UUID userId, UserType userType, UUID correlationId) {
		switch (userType) {
			case MASTER -> { /* 마스터는 모든 데이터 조회 가능 */ }
			case HUB -> {
				if (correlationId != null) {
					builder.and(order.supplier.hubInfo.hubId.eq(correlationId)
							.or(order.receiver.hubInfo.hubId.eq(correlationId)));
				} else {
					builder.and(order.orderId.isNull()); // 권한 식별자 없으면 빈 결과
				}
			}
			case COMPANY -> {
				// 업체 담당자는 본인이 생성한 주문만 조회 가능
				builder.and(order.createdBy.eq(userId));
			}
			case DELIVERY -> {
				// 배송 담당자 목록 검색은 현재 구조에서 별도 배송 서비스 연동이 필요하므로 안전하게 빈 결과 반환
				builder.and(order.orderId.isNull());
			}
			default -> builder.and(order.orderId.isNull());
		}
	}

	public static OrderSpecifier<?>[] getOrderSpecifier(Sort sort) {
		return sort.stream()
				.map(orderSort -> {
					com.querydsl.core.types.Order direction = orderSort.isAscending() ? ASC : DESC;

					return switch (orderSort.getProperty()) {
						case "createdAt" -> new OrderSpecifier<>(direction, order.createdAt);
						case "updatedAt" -> new OrderSpecifier<>(direction, order.updatedAt);
						case "supplierName" -> new OrderSpecifier<>(direction, order.supplier.supplierName);
						case "receiverName" -> new OrderSpecifier<>(direction, order.receiver.receiverName);
						case "itemName" -> new OrderSpecifier<>(direction, order.orderItem.orderItemInfo.itemName);
						case "status" -> new OrderSpecifier<>(direction, order.status);
						default -> new OrderSpecifier<>(DESC, order.createdAt);
					};
				})
				.toArray(OrderSpecifier[]::new);
	}

	private static BooleanExpression keywordContains(String keyword) {
		return StringUtils.hasText(keyword) ?
				order.supplier.supplierName.containsIgnoreCase(keyword)
						.or(order.receiver.receiverName.containsIgnoreCase(keyword))
						.or(order.orderItem.orderItemInfo.itemName.containsIgnoreCase(keyword)) : null;
	}

	private static BooleanExpression supplierNameContains(String supplierName) {
		return StringUtils.hasText(supplierName) ? order.supplier.supplierName.containsIgnoreCase(supplierName) : null;
	}

	private static BooleanExpression receiverNameContains(String receiverName) {
		return StringUtils.hasText(receiverName) ? order.receiver.receiverName.containsIgnoreCase(receiverName) : null;
	}

	private static BooleanExpression itemNameContains(String itemName) {
		return StringUtils.hasText(itemName) ? order.orderItem.orderItemInfo.itemName.containsIgnoreCase(itemName) : null;
	}

	private static BooleanExpression statusEq(OrderStatus status) {
		return status != null ? order.status.eq(status) : null;
	}
}
