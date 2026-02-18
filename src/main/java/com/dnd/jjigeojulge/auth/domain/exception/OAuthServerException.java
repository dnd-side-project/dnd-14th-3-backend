package com.dnd.jjigeojulge.auth.domain.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class OAuthServerException extends OAuthException {

	public OAuthServerException() {
		super(ErrorCode.OAUTH_SERVER_ERROR);
	}

	public OAuthServerException(Throwable cause) {
		super(ErrorCode.OAUTH_SERVER_ERROR, cause);
	}
}