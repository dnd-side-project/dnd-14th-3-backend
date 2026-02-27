package com.dnd.jjigeojulge.reservation.domain.vo;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduledTime {

	@Column(name = "scheduled_at", nullable = false)
	private LocalDateTime time;

	private ScheduledTime(LocalDateTime time) {
		this.time = time;
	}

	public static ScheduledTime of(LocalDateTime time, LocalDateTime now) {
		validate(time, now);
		return new ScheduledTime(time);
	}

	private static void validate(LocalDateTime time, LocalDateTime now) {
		if (time == null) {
			throw new IllegalArgumentException("예약 시간은 필수입니다.");
		}
		if (time.isBefore(now)) {
			throw new IllegalArgumentException("과거 시간으로 예약할 수 없습니다.");
		}
		if (time.getMinute() % 30 != 0 || time.getSecond() != 0) {
			throw new IllegalArgumentException("예약 시간은 30분 단위여야 합니다.");
		}
	}

	public boolean isExpired(LocalDateTime now) {
		return this.time.isBefore(now) || this.time.isEqual(now);
	}
}
