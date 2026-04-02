package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.vo.UserType;

import java.util.UUID;

// 주문 도메인에서의 마스터 관리자와 허브 관리자를 대상으로 권한 검증 수행
// 마스터 관리자: 권한이 MASTER면 통과
// 허브관리자: 권한이 HUB면서 공급업체 측 허브ID가 허브관리자의 허브ID와 일치할 때 통과
public interface OrderAccess {

	boolean checkAuthority(UUID userId, UserType userType, UUID supplierHubId);

}
