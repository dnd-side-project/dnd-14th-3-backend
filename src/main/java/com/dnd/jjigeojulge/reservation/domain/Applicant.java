package com.dnd.jjigeojulge.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.dnd.jjigeojulge.global.common.entity.BaseUpdatableEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "applicant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Applicant extends BaseUpdatableEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reservation_id", nullable = false)
	private Reservation reservation;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ApplicantStatus status;

	private Applicant(Reservation reservation, Long userId, ApplicantStatus status) {
		this.reservation = reservation;
		this.userId = userId;
		this.status = status;
	}

	public static Applicant create(Reservation reservation, Long userId) {
		validate(reservation, userId);
		return new Applicant(reservation, userId, ApplicantStatus.APPLIED);
	}

	private static void validate(Reservation reservation, Long userId) {
		if (reservation == null) {
			throw new IllegalArgumentException("예약 정보는 필수입니다.");
		}
		if (userId == null) {
			throw new IllegalArgumentException("지원자 사용자 ID는 필수입니다.");
		}
	}

	// 작성자(Owner)가 이 지원자를 선택(수락)했을 때 호출
	public void markAsSelected() {
		if (this.status != ApplicantStatus.APPLIED) {
			throw new IllegalStateException("지원 대기 중(APPLIED)인 상태에서만 선택할 수 있습니다.");
		}
		this.status = ApplicantStatus.SELECTED;
	}

	// 작성자(Owner)가 다른 사람을 수락하여, 이 지원자가 자동으로 거절되었을 때 호출
	public void markAsRejected() {
		if (this.status != ApplicantStatus.APPLIED) {
			throw new IllegalStateException("지원 대기 중(APPLIED)인 상태에서만 거절 처리할 수 있습니다.");
		}
		this.status = ApplicantStatus.REJECTED;
	}

	// 지원자(Applicant) 본인이 지원을 취소했을 때 호출
	public void cancelApplication() {
		if (this.status != ApplicantStatus.APPLIED) {
			throw new IllegalStateException("지원 대기 중(APPLIED)일 때만 지원을 취소할 수 있습니다.");
		}
		this.status = ApplicantStatus.CANCELED;
	}
}
