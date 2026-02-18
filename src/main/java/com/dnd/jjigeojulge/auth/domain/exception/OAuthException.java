package com.dnd.jjigeojulge.auth.domain.exception;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

public abstract class OAuthException extends BusinessException {

	protected OAuthException(ErrorCode errorCode) {
		super(errorCode);
	}

	protected OAuthException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}
}