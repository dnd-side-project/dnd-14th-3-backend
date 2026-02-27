package com.dnd.jjigeojulge.reservation.domain.exception;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class ReservationAccessDeniedException extends BusinessException {

    public ReservationAccessDeniedException() {
        super(ErrorCode.NOT_MATCH_REQUEST_OWNER); // Re-using FORBIDDEN code for now
    }
}
