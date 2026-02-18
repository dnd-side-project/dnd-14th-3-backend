package com.dnd.jjigeojulge.matchrequest.domain;

import java.math.BigDecimal;

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

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@Builder
	public MatchRequest(BigDecimal latitude, BigDecimal longitude, String specificPlace, MatchRequestStatus status,
		ShootingDurationOption expectedDuration, String requestMessage, User user) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.specificPlace = specificPlace;
		this.status = status;
		this.expectedDuration = expectedDuration;
		this.requestMessage = requestMessage;
		this.user = user;
	}
}
