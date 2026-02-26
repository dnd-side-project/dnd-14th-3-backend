package com.dnd.jjigeojulge.reservation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
	RECRUITING("모집 중"),
	CONFIRMED("확정됨"),
	RECRUITMENT_CLOSED("모집 마감"),
	CANCELED("취소됨"),
	COMPLETED("완료됨");

	private final String description;

	public boolean isRecruiting() {
		return this == RECRUITING;
	}
}
