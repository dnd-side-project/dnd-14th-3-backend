package com.dnd.jjigeojulge.matchrequest.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchRequestForbiddenException extends MatchRequestException {

	public MatchRequestForbiddenException() {
		super(ErrorCode.NOT_MATCH_REQUEST_OWNER);
	}
}
