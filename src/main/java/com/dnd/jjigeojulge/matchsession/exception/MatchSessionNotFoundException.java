package com.dnd.jjigeojulge.matchsession.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchSessionNotFoundException extends MatchSessionException {

	public MatchSessionNotFoundException() {
		super(ErrorCode.MATCH_SESSION_NOT_FOUND);
	}

	public MatchSessionNotFoundException(Throwable throwable) {
		super(ErrorCode.MATCH_SESSION_NOT_FOUND, throwable);
	}
}
