package com.dnd.jjigeojulge.user.domain.exception;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class UserNotFoundException extends UserException {

	public UserNotFoundException() {
		super(ErrorCode.USER_NOT_FOUND);
	}

}
