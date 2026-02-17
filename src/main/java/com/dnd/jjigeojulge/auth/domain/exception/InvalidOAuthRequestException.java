package com.dnd.jjigeojulge.auth.domain.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class InvalidOAuthRequestException extends OAuthException {
	public InvalidOAuthRequestException() {
		super(ErrorCode.INVALID_OAUTH_REQUEST);
	}
}
