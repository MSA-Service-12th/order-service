package com.loopang.orderservice.infrastructure.persistence;

import static com.loopang.orderservice.domain.entity.QOrder.order;

import com.loopang.orderservice.application.dto.OrderSearchConditionDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.repository.OrderQueryRepository;
import com.loopang.orderservice.domain.vo.UserType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

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
	public Page<Order> findAllOrders(OrderSearchConditionDto condition, Pageable pageable,
									 UUID userId, UserType userType, UUID correlationId) {
		// 검색 조건 설정용
		BooleanBuilder booleanBuilder = OrderQueryCondition.createSearchCondition(condition);

		// 인가 기반 데이터 격리 필터 추가
		OrderQueryCondition.applyAuthorizationFilter(booleanBuilder, userId, userType, correlationId);

		List<Order> content = queryFactory
				.selectFrom(order)
				.where(order.deletedAt.isNull(), booleanBuilder)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.orderBy(OrderQueryCondition.getOrderSpecifier(pageable.getSort()))
				.fetch();

		JPAQuery<Long> countQuery = queryFactory
				.select(order.count())
				.from(order)
				.where(order.deletedAt.isNull(), booleanBuilder);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}
}
