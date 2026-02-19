package com.dnd.jjigeojulge.matchproposal.exception;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class MatchProposalException extends BusinessException {

	public MatchProposalException(ErrorCode errorCode) {
		super(errorCode);
	}

	public MatchProposalException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}
}
