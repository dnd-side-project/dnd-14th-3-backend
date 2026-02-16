package com.dnd.jjigeojulge.user.domain.exception;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class InvalidPhotoStyleException extends BusinessException {

	public InvalidPhotoStyleException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidPhotoStyleException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}
}
