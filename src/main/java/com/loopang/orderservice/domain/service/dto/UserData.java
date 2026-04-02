package com.loopang.orderservice.domain.service.dto;

import com.loopang.orderservice.domain.vo.UserType;

import java.util.UUID;

// 허브관리자가 로그인한 상태에서 물품 수령할 업체 담당자의 정보를 조회하기 위해 필요할 수 있음
public record UserData(UUID userId, UserType userType, String slackId) {
}
