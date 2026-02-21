package com.dnd.jjigeojulge.reservation.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestMessage {

	private static final int MAX_LENGTH = 500;

	@Column(name = "request_message", length = 500)
	private String value;

	private RequestMessage(String value) {
		this.value = value;
	}

	public static RequestMessage from(String value) {
		validate(value);
		return new RequestMessage(value);
	}

	private static void validate(String value) {
		if (value != null && value.length() > MAX_LENGTH) {
			throw new IllegalArgumentException("요청 메시지는 " + MAX_LENGTH + "자 이하여야 합니다.");
		}
	}
}
