package com.dnd.jjigeojulge.matchrequest.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.dnd.jjigeojulge.global.common.dto.GeoPoint;
import com.dnd.jjigeojulge.global.common.entity.BaseUpdatableEntity;
import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "match_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchRequest extends BaseUpdatableEntity {

	@Column(name = "latitude", precision = 10, scale = 6, nullable = false)
	private BigDecimal latitude;

	@Column(name = "longitude", precision = 10, scale = 6, nullable = false)
	private BigDecimal longitude;

	@Column(name = "specific_place", length = 150, nullable = false)
	private String specificPlace;

	@Column(name = "status", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private MatchRequestStatus status;

	@Column(name = "expected_duration", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private ShootingDurationOption expectedDuration;

	@Column(name = "request_message", columnDefinition = "text")
	private String requestMessage;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@Builder
	public MatchRequest(BigDecimal latitude, BigDecimal longitude, String specificPlace, MatchRequestStatus status,
		ShootingDurationOption expectedDuration, String requestMessage, LocalDateTime expiresAt, User user) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.specificPlace = specificPlace;
		this.status = status;
		this.expectedDuration = expectedDuration;
		this.requestMessage = requestMessage;
		this.expiresAt = expiresAt;
		this.user = user;
	}

	public void cancel() {
		this.status = MatchRequestStatus.CANCELLED;
	}

	public void expire() {
		this.status = MatchRequestStatus.EXPIRED;
	}

	public void match() {
		this.status = MatchRequestStatus.MATCHED;
	}

	public boolean isExpired(LocalDateTime now) {
		// 이미 최종 상태(매칭 완료, 취소 등)라면 만료 대상이 아님
		if (this.status == MatchRequestStatus.CANCELLED || this.status == MatchRequestStatus.MATCHED) {
			return false;
		}
		// 명시적으로 EXPIRED 상태이거나, 시간이 지났다면 '만료'로 간주
		return this.status == MatchRequestStatus.EXPIRED || !this.expiresAt.isAfter(now);
	}

	public void retry(LocalDateTime nextExpiresAt) {
		// 재시도 시 다시 WAITING 상태로 복구하고 시간 연장
		this.status = MatchRequestStatus.WAITING;
		this.expiresAt = nextExpiresAt;
	}

	public boolean isOwner(Long userId) {
		return Objects.equals(this.user.getId(), userId);
	}

	public GeoPoint toGeoPoint() {
		return new GeoPoint(
			this.latitude.doubleValue(),
			this.longitude.doubleValue()
		);
	}

	public void extendExpiration() {
		this.expiresAt = this.expiresAt.plusMinutes(5L);
	}
}
