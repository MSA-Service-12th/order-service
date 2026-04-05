package com.loopang.orderservice.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Contact {

	@Column(name = "receiver_slack_id", length = 50, nullable = false)
	private String slackId;

	public static Contact of(String slackId) {
		return new Contact(slackId);
	}
}
