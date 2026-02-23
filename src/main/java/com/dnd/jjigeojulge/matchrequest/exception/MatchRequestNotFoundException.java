package com.dnd.jjigeojulge.matchrequest.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchRequestNotFoundException extends MatchRequestException {
	public MatchRequestNotFoundException() {
		super(ErrorCode.MATCH_REQUEST_NOT_FOUND);
	}

	public MatchRequestNotFoundException(Throwable cause) {
		super(ErrorCode.MATCH_REQUEST_NOT_FOUND, cause);
	}
}
