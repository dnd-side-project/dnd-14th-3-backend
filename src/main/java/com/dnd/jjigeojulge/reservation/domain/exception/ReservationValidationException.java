package com.dnd.jjigeojulge.reservation.domain.exception;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class ReservationValidationException extends BusinessException {

    public ReservationValidationException() {
        super(ErrorCode.RESERVATION_VALIDATION_FAILED);
    }

    public ReservationValidationException(String customMessage) {
        super(ErrorCode.RESERVATION_VALIDATION_FAILED, customMessage);
    }
}
