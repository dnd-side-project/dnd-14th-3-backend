package com.dnd.jjigeojulge.user.domain;

public enum UserStatus {
	/** 온보딩이 완료되지 않은 신규 가입자 (추가 정보 입력 필요) */
	PENDING,

	/** 모든 정보 입력이 완료된 정식 회원 */
	ACTIVE,

	/** 휴면 상태 */
	DORMANT,

	/** 탈퇴한 회원 */
	DELETED
}
