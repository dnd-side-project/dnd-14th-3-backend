package com.dnd.jjigeojulge.notification.domain.exception;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

public class NotificationValidationException extends BusinessException {

    public NotificationValidationException(String customMessage) {
        super(ErrorCode.NOTIFICATION_VALIDATION_FAILED, customMessage);
    }
}
