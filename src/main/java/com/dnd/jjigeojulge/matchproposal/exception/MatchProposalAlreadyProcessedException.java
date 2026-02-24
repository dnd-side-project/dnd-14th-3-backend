package com.dnd.jjigeojulge.matchproposal.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchProposalAlreadyProcessedException extends MatchProposalException {
	public MatchProposalAlreadyProcessedException() {
		super(ErrorCode.MATCH_PROPOSAL_ALREADY_PROCESSED);
	}

	public MatchProposalAlreadyProcessedException(Throwable cause) {
		super(ErrorCode.MATCH_PROPOSAL_ALREADY_PROCESSED, cause);
	}
}
