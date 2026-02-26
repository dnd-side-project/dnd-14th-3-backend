package com.dnd.jjigeojulge.matchproposal.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchProposalAlreadyRejectedException extends MatchProposalException {

	public MatchProposalAlreadyRejectedException() {
		super(ErrorCode.MATCH_PROPOSAL_REJECTED_BY_OPPONENT);
	}

	public MatchProposalAlreadyRejectedException(Throwable cause) {
		super(ErrorCode.MATCH_PROPOSAL_REJECTED_BY_OPPONENT, cause);
	}
}
