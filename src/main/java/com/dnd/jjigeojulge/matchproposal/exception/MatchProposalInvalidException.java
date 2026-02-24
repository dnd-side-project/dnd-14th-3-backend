package com.dnd.jjigeojulge.matchproposal.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchProposalInvalidException extends MatchProposalException {
	public MatchProposalInvalidException() {
		super(ErrorCode.INVALID_MATCH_PARTICIPANT);
	}

	public MatchProposalInvalidException(Throwable cause) {
		super(ErrorCode.INVALID_MATCH_PARTICIPANT, cause);
	}
}
