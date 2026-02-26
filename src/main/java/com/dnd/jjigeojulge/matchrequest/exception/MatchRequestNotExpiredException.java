package com.dnd.jjigeojulge.matchrequest.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchRequestNotExpiredException extends MatchRequestException {

	public MatchRequestNotExpiredException() {
		super(ErrorCode.MATCH_REQUEST_NOT_EXPIRED);
	}
}
