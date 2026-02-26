package com.dnd.jjigeojulge.event;

import com.dnd.jjigeojulge.matchproposal.data.MatchProposalDto;

public record MatchProposalAcceptedEvent(
	Long actorUserId,
	MatchProposalDto matchProposalDto
) {
}
