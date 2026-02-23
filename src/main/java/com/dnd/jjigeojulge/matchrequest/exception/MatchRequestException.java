package com.dnd.jjigeojulge.matchrequest.exception;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchRequestException extends BusinessException {
	public MatchRequestException(ErrorCode errorCode) {
		super(errorCode);
	}

	public MatchRequestException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}
}
