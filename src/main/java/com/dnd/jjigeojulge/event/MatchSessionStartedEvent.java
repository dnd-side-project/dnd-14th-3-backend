package com.dnd.jjigeojulge.event;

import com.dnd.jjigeojulge.matchsession.domain.MatchSessionStatus;

public record MatchSessionStartedEvent(
	Long sessionId,
	Long userId,
	MatchSessionStatus status
) {
}
