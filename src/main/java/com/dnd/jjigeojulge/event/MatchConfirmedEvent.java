package com.dnd.jjigeojulge.event;

import com.dnd.jjigeojulge.matchsession.data.MatchSessionDto;

public record MatchConfirmedEvent(
	MatchSessionDto matchSessionDto
) {
}
