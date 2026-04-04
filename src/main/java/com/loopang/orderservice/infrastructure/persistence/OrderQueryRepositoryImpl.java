package com.loopang.orderservice.infrastructure.persistence;

import static com.loopang.orderservice.domain.entity.QOrder.order;
import static com.querydsl.core.types.Order.*;

import com.loopang.orderservice.application.dto.OrderSearchConditionDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.repository.OrderQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Order> findById(UUID orderId) {
		return Optional.ofNullable(
				queryFactory
						.selectFrom(order)
						.where(order.orderId.eq(orderId), order.deletedAt.isNull())
						.fetchOne()
		);
	}

	@Override
	public Page<Order> findAllOrders(OrderSearchConditionDto condition, Pageable pageable) {
		BooleanBuilder booleanBuilder = createSearchCondition(condition);

		List<Order> content = queryFactory
				.selectFrom(order)
				.where(order.deletedAt.isNull(), booleanBuilder)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.orderBy(getOrderSpecifier(pageable.getSort()))
				.fetch();

		JPAQuery<Long> countQuery = queryFactory
				.select(order.count())
				.from(order)
				.where(order.deletedAt.isNull(), booleanBuilder);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	private BooleanBuilder createSearchCondition(OrderSearchConditionDto condition) {
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (condition == null) {
			return booleanBuilder;
		}

		// 통합 키워드 검색 (공급업체명, 수령업체명, 상품명)
		String keyword = condition.keyword();
		if (StringUtils.hasText(keyword)) {
			booleanBuilder.and(
					order.supplier.supplierName.containsIgnoreCase(keyword)
							.or(order.receiver.receiverName.containsIgnoreCase(keyword))
							.or(order.orderItem.orderItemInfo.itemName.containsIgnoreCase(keyword))
			);
		}

		// 개별 필터 검색
		if (StringUtils.hasText(condition.supplierName())) {
			booleanBuilder.and(order.supplier.supplierName.containsIgnoreCase(condition.supplierName()));
		}

		if (StringUtils.hasText(condition.receiverName())) {
			booleanBuilder.and(order.receiver.receiverName.containsIgnoreCase(condition.receiverName()));
		}

		if (StringUtils.hasText(condition.itemName())) {
			booleanBuilder.and(order.orderItem.orderItemInfo.itemName.containsIgnoreCase(condition.itemName()));
		}

		if (condition.status() != null) {
			booleanBuilder.and(order.status.eq(condition.status()));
		}

		return booleanBuilder;
	}

	private OrderSpecifier<?>[] getOrderSpecifier(Sort sort) {
		return sort.stream()
				.map(orderSort -> {
					PathBuilder<Order> pathBuilder = new PathBuilder<>(Order.class, "order");

					return new OrderSpecifier<>(
							orderSort.isAscending() ? ASC : DESC,
							pathBuilder.get(orderSort.getProperty(), Comparable.class)
					);
				})
				.toArray(OrderSpecifier[]::new);
	}
}
