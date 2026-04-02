package com.loopang.orderservice.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Contact {

	private UUID companyManagerId;
	private String slackId;

	public static Contact of(UUID companyManagerId, String slackId) {
		return new Contact(companyManagerId, slackId);
	}
}
