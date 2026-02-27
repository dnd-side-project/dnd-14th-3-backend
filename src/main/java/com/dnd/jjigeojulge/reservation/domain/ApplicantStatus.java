package com.dnd.jjigeojulge.reservation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicantStatus {
	APPLIED("지원"),
	SELECTED("선택됨"),
	REJECTED("미선택"),
	CANCELED("취소");

	private final String description;
}
