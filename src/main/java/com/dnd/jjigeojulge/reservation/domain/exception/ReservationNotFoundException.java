package com.dnd.jjigeojulge.reservation.domain.exception;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class ReservationNotFoundException extends BusinessException {
    public ReservationNotFoundException() {
        super(ErrorCode.RESERVATION_NOT_FOUND);
    }
}
