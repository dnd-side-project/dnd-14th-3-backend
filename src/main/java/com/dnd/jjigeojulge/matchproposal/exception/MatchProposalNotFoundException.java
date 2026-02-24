package com.dnd.jjigeojulge.matchproposal.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchProposalNotFoundException extends MatchProposalException {

	public MatchProposalNotFoundException() {
		super(ErrorCode.MATCH_PROPOSAL_NOT_FOUND);
	}

	public MatchProposalNotFoundException(Throwable cause) {
		super(ErrorCode.MATCH_PROPOSAL_NOT_FOUND, cause);
	}
}
