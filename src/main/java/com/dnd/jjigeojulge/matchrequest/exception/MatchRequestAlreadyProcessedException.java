package com.dnd.jjigeojulge.matchrequest.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchRequestAlreadyProcessedException extends MatchRequestException {
	public MatchRequestAlreadyProcessedException() {
		super(ErrorCode.MATCH_REQUEST_ALREADY_PROCESSED);
	}

	public MatchRequestAlreadyProcessedException(Throwable cause) {
		super(ErrorCode.MATCH_REQUEST_ALREADY_PROCESSED, cause);
	}
}
