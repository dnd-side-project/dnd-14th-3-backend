package com.dnd.jjigeojulge.event.listener;

import com.dnd.jjigeojulge.matchsession.data.MatchSessionDto;

public record MatchConfirmedEvent(
	MatchSessionDto matchSessionDto
) {
}
