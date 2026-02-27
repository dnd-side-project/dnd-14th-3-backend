package com.dnd.jjigeojulge.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    RESERVATION_APPLIED("동행 신청"),
    RESERVATION_ACCEPTED("동행 확정"),
    RESERVATION_REJECTED("동행 거절"),
    RESERVATION_CANCELED("동행 취소"),
    COMMENT_ADDED("댓글 작성");

    private final String description;
}
