package com.dnd.jjigeojulge.matchsession.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.entity.BaseUpdatableEntity;
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
@Table(name = "match_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchSession extends BaseUpdatableEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private MatchSessionStatus status;

	@Column(name = "dest_latitude", precision = 10, scale = 6)
	private BigDecimal destLatitude;

	@Column(name = "dest_longitude", precision = 10, scale = 6)
	private BigDecimal destLongitude;

	@Column(name = "matched_at", nullable = false)
	private LocalDateTime matchedAt;

	@Column(name = "ended_at")
	private LocalDateTime endedAt;

	@Column(name = "is_arrived_a")
	private boolean isArrivedA = false;

	@Column(name = "is_arrived_b")
	private boolean isArrivedB = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_a_id", nullable = false)
	private User userA;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_b_id", nullable = false)
	private User userB;

	@Builder
	public MatchSession(MatchSessionStatus status, BigDecimal destLatitude, BigDecimal destLongitude,
		LocalDateTime matchedAt, LocalDateTime endedAt, boolean isArrivedA, boolean isArrivedB, User userA,
		User userB) {
		this.status = status;
		this.destLatitude = destLatitude;
		this.destLongitude = destLongitude;
		this.matchedAt = matchedAt;
		this.endedAt = endedAt;
		this.isArrivedA = isArrivedA;
		this.isArrivedB = isArrivedB;
		this.userA = userA;
		this.userB = userB;
	}

	public static MatchSession create(User userA, User userB, BigDecimal lat, BigDecimal lon) {
		return MatchSession.builder()
			.status(MatchSessionStatus.ACTIVE)
			.userA(userA)
			.userB(userB)
			.destLatitude(lat)
			.destLongitude(lon)
			.matchedAt(LocalDateTime.now())
			.isArrivedA(false)
			.isArrivedB(false)
			.build();
	}

	public void arrive(Long userId) {
		if (userA.getId().equals(userId)) {
			isArrivedA = true;
		} else if (userB.getId().equals(userId)) {
			isArrivedB = true;
		} else {
			throw new IllegalArgumentException("세션 참가자가 아님");
		}

		if (isArrivedA && isArrivedB) {
			this.status = MatchSessionStatus.ARRIVED;
		}
	}

	public void end() {
		this.status = MatchSessionStatus.ENDED;
		this.endedAt = LocalDateTime.now();
	}

	public boolean isParticipant(Long userId) {
		return userA.getId().equals(userId) || userB.getId().equals(userId);
	}
}
