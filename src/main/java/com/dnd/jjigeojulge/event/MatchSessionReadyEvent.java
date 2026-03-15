package com.dnd.jjigeojulge.event;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.matchsession.domain.MatchSessionStatus;

public record MatchSessionReadyEvent(
	Long sessionId,
	Long senderId,
	MatchSessionStatus status, // ARRIVED 상태를 전달
	LocalDateTime readyAt
) {

	public MatchSessionReadyEvent(Long sessionId, Long senderId, MatchSessionStatus status) {
		this(sessionId, senderId, status, LocalDateTime.now());
	}
}
