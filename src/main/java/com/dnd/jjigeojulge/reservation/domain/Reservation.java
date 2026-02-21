package com.dnd.jjigeojulge.reservation.domain;

import java.util.ArrayList;
import java.util.List;

import com.dnd.jjigeojulge.global.common.entity.BaseUpdatableEntity;
import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.vo.PlaceInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.RequestMessage;
import com.dnd.jjigeojulge.reservation.domain.vo.ScheduledTime;
import com.dnd.jjigeojulge.user.domain.StyleName;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseUpdatableEntity {

	@Column(name = "owner_id", nullable = false)
	private Long ownerId;

	@Embedded
	private ScheduledTime scheduledTime;

	@Embedded
	private PlaceInfo placeInfo;

	@Enumerated(EnumType.STRING)
	@Column(name = "shooting_duration", nullable = false)
	private ShootingDurationOption shootingDuration;

	@Embedded
	private RequestMessage requestMessage;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "reservation_photo_style_snapshot", joinColumns = @JoinColumn(name = "reservation_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "style_name", nullable = false)
	private List<StyleName> photoStyleSnapshot = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ReservationStatus status;

	private Reservation(
		Long ownerId,
		ScheduledTime scheduledTime,
		PlaceInfo placeInfo,
		ShootingDurationOption shootingDuration,
		RequestMessage requestMessage,
		List<StyleName> photoStyleSnapshot,
		ReservationStatus status
	) {
		this.ownerId = ownerId;
		this.scheduledTime = scheduledTime;
		this.placeInfo = placeInfo;
		this.shootingDuration = shootingDuration;
		this.requestMessage = requestMessage;
		this.photoStyleSnapshot = new ArrayList<>(photoStyleSnapshot);
		this.status = status;
	}

	public static Reservation create(
		Long ownerId,
		ScheduledTime scheduledTime,
		PlaceInfo placeInfo,
		ShootingDurationOption shootingDuration,
		RequestMessage requestMessage,
		List<StyleName> photoStyleSnapshot
	) {
		validateCreation(ownerId, photoStyleSnapshot);
		return new Reservation(
			ownerId,
			scheduledTime,
			placeInfo,
			shootingDuration,
			requestMessage,
			photoStyleSnapshot,
			ReservationStatus.RECRUITING
		);
	}

	private static void validateCreation(Long ownerId, List<StyleName> photoStyleSnapshot) {
		if (ownerId == null) {
			throw new IllegalArgumentException("작성자(Owner) ID는 필수입니다.");
		}
		if (photoStyleSnapshot == null || photoStyleSnapshot.isEmpty()) {
			throw new IllegalArgumentException("촬영 유형 스냅샷은 최소 1개 이상 존재해야 합니다.");
		}
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
		if (!this.ownerId.equals(requesterId)) {
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
