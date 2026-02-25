package com.dnd.jjigeojulge.reservation.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationTitle {

    private static final int MAX_LENGTH = 50;

    @Column(name = "title", length = 50, nullable = false)
    private String value;

    private ReservationTitle(String value) {
        this.value = value;
    }

    public static ReservationTitle from(String value) {
        validate(value);
        return new ReservationTitle(value);
    }

    private static void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("예약 제목은 필수입니다.");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("예약 제목은 " + MAX_LENGTH + "자 이하여야 합니다.");
        }
    }
}
