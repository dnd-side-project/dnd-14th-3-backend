package com.dnd.jjigeojulge.reservation.application.dto.query;

public enum AppliedReservationStatus {
    WAITING("대기중"),
    MATCHED("매칭 확정"),
    COMPLETED("일정 완료"),
    REJECTED("매칭 실패"),
    CANCELED("예약 취소됨");

    private final String description;

    AppliedReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
