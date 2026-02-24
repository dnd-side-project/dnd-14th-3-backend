package com.dnd.jjigeojulge.event;

import java.time.LocalDateTime;

public record MatchRequestExpiredEvent(
	Long userId,
	Long matchRequestId,
	LocalDateTime expiresAt
) {
}
