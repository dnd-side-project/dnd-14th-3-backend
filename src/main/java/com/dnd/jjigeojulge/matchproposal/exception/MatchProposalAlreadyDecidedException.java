package com.dnd.jjigeojulge.matchproposal.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchProposalAlreadyDecidedException extends MatchProposalException {

	public MatchProposalAlreadyDecidedException() {
		super(ErrorCode.MATCH_PROPOSAL_ALREADY_DECIDED);
	}

	public MatchProposalAlreadyDecidedException(Throwable cause) {
		super(ErrorCode.MATCH_PROPOSAL_ALREADY_DECIDED, cause);
	}
}
