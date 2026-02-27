package com.dnd.jjigeojulge.event;

import java.time.LocalDateTime;

public record MatchSessionUserArrivedEvent(
	Long sessionId,
	Long userId,      // 도착한 유저의 ID
	LocalDateTime arrivedAt
) {
	public MatchSessionUserArrivedEvent(Long sessionId, Long userId) {
		this(sessionId, userId, LocalDateTime.now());
	}
}
