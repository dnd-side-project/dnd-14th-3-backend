package com.dnd.jjigeojulge.reservation.domain;

import com.dnd.jjigeojulge.global.common.entity.BaseUpdatableEntity;
import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.vo.OwnerInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.PlaceInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.RequestMessage;
import com.dnd.jjigeojulge.reservation.domain.vo.ScheduledTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseUpdatableEntity {

	@Embedded
	private OwnerInfo ownerInfo;

	@Embedded
	private ScheduledTime scheduledTime;

	@Embedded
	private PlaceInfo placeInfo;

	@Enumerated(EnumType.STRING)
	@Column(name = "shooting_duration", nullable = false)
	private ShootingDurationOption shootingDuration;

	@Embedded
	private RequestMessage requestMessage;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ReservationStatus status;

	private Reservation(
		OwnerInfo ownerInfo,
		ScheduledTime scheduledTime,
		PlaceInfo placeInfo,
		ShootingDurationOption shootingDuration,
		RequestMessage requestMessage,
		ReservationStatus status
	) {
		this.ownerInfo = ownerInfo;
		this.scheduledTime = scheduledTime;
		this.placeInfo = placeInfo;
		this.shootingDuration = shootingDuration;
		this.requestMessage = requestMessage;
		this.status = status;
	}

	public static Reservation create(
		OwnerInfo ownerInfo,
		ScheduledTime scheduledTime,
		PlaceInfo placeInfo,
		ShootingDurationOption shootingDuration,
		RequestMessage requestMessage
	) {
		if (ownerInfo == null) {
			throw new IllegalArgumentException("작성자(Owner) 정보는 필수입니다.");
		}
		return new Reservation(
			ownerInfo,
			scheduledTime,
			placeInfo,
			shootingDuration,
			requestMessage,
			ReservationStatus.RECRUITING
		);
	}

	public void update(
		ScheduledTime scheduledTime,
		PlaceInfo placeInfo,
		ShootingDurationOption shootingDuration,
		RequestMessage requestMessage
	) {
		if (!this.status.isRecruiting()) {
			throw new IllegalStateException("모집 중(RECRUITING)인 상태에서만 예약 정보를 수정할 수 있습니다.");
		}
		this.scheduledTime = scheduledTime;
		this.placeInfo = placeInfo;
		this.shootingDuration = shootingDuration;
		this.requestMessage = requestMessage;
	}

	public void cancel(Long requesterId) {
		if (!this.ownerInfo.getUserId().equals(requesterId)) {
			throw new IllegalArgumentException("예약 작성자 본인만 예약을 취소할 수 있습니다.");
		}
		if (!this.status.isRecruiting()) {
			throw new IllegalStateException("모집 중(RECRUITING)인 상태에서만 예약을 취소할 수 있습니다.");
		}
		this.status = ReservationStatus.CANCELED;
	}

	// Commit 4에서 추가될 상태 변경 메서드들 (확정 처리 등)을 위한 보호된(protected/package-private) Setter성 메서드 대용
	protected void changeStatusToConfirmed() {
		this.status = ReservationStatus.CONFIRMED;
	}
}
