package com.dnd.jjigeojulge.reservation.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMessageTest {

	@Test
	@DisplayName("500자 이하의 메시지는 정상 생성된다")
	void create_Success() {
		// given
		String validMessage = "안녕하세요! 잘 부탁드립니다.";

		// when
		RequestMessage message = RequestMessage.from(validMessage);

		// then
		assertThat(message.getValue()).isEqualTo(validMessage);
	}

	@Test
	@DisplayName("null 메시지도 정상 생성된다")
	void create_Success_Null() {
		// when
		RequestMessage message = RequestMessage.from(null);

		// then
		assertThat(message.getValue()).isNull();
	}

	@Test
	@DisplayName("500자를 초과하는 메시지는 예외가 발생한다")
	void create_Fail_ExceedMaxLength() {
		// given
		String longMessage = "a".repeat(501);

		// when & then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> RequestMessage.from(longMessage))
			.withMessage("요청 메시지는 500자 이하여야 합니다.");
	}
}
