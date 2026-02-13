package com.dnd.jjigeojulge.global.exception.user;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class UserException extends BusinessException {
	public UserException(ErrorCode errorCode) {
		super(errorCode);
	}

	public UserException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}
}
