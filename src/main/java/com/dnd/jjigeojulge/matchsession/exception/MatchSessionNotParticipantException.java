package com.dnd.jjigeojulge.matchsession.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchSessionNotParticipantException extends MatchSessionException {
	public MatchSessionNotParticipantException() {
		super(ErrorCode.MATCH_SESSION_NOT_PARTICIPANT);
	}

	public MatchSessionNotParticipantException(Throwable cause) {
		super(ErrorCode.MATCH_SESSION_NOT_PARTICIPANT, cause);
	}
}
