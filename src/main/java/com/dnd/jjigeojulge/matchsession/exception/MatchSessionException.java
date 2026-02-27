package com.dnd.jjigeojulge.matchsession.exception;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchSessionException extends BusinessException {
	public MatchSessionException(ErrorCode errorCode) {
		super(errorCode);
	}

	public MatchSessionException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}
}
