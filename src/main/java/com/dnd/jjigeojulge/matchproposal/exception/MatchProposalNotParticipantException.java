package com.dnd.jjigeojulge.matchproposal.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchProposalNotParticipantException extends MatchProposalException {
	public MatchProposalNotParticipantException() {
		super(ErrorCode.MATCH_PROPOSAL_NOT_PARTICIPANT);
	}

	public MatchProposalNotParticipantException(Throwable cause) {
		super(ErrorCode.MATCH_PROPOSAL_NOT_PARTICIPANT, cause);
	}
}
