package com.dnd.jjigeojulge.event;

import com.dnd.jjigeojulge.matchproposal.data.MatchProposalDto;

public record MatchProposalRejectedEvent(
	Long actorUserId,
	MatchProposalDto matchProposalDto
) {
}
