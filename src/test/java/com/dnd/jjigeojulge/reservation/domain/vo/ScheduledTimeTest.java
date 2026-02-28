package com.dnd.jjigeojulge.reservation.domain.vo;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScheduledTimeTest {

	@Test
	@DisplayName("현재 시간보다 미래인 경우 정상 생성된다")
	void create_Success() {
		// given
		LocalDateTime now = LocalDateTime.of(2026, 2, 21, 12, 0, 0);
		LocalDateTime futureTime = LocalDateTime.of(2026, 2, 21, 14, 30, 0);

		// when
		ScheduledTime scheduledTime = ScheduledTime.of(futureTime, now);

		// then
		assertThat(scheduledTime.getTime()).isEqualTo(futureTime);
	}

	@Test
	@DisplayName("현재 시간보다 과거인 경우 예외가 발생한다")
	void create_Fail_PastTime() {
		// given
		LocalDateTime now = LocalDateTime.of(2026, 2, 21, 12, 0, 0);
		LocalDateTime pastTime = LocalDateTime.of(2026, 2, 21, 11, 30, 0);

		// when & then
		assertThatIllegalArgumentException()
				.isThrownBy(() -> ScheduledTime.of(pastTime, now))
				.withMessage("과거 시간으로 예약할 수 없습니다.");
	}

}
