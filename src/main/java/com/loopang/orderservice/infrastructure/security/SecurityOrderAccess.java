package com.loopang.orderservice.infrastructure.security;

import com.loopang.orderservice.domain.service.OrderAccess;
import com.loopang.orderservice.domain.vo.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityOrderAccess implements OrderAccess {

	// 요청 헤더를 통해 수신한 인증 정보를 활용
	// 권한 검증에 필요하지만 요청 헤더에는 없는 사용자 정보는 UserClient를 통해 조회
	// (허브관리자: 담당 허브에 관한 조회, 수정, 삭제만 가능 -> 허브관리자의 담당 허브 확인용)

	@Override
	public boolean checkAuthority(UUID userId, UserType userType, UUID supplierHubId) {
		return false;
	}
}
