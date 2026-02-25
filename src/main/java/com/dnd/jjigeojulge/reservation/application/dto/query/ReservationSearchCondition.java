package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDate;

import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.PhotoStyle;

import lombok.Builder;

@Builder
public record ReservationSearchCondition(
        Region1Depth region1Depth,
        LocalDate date,
        Gender gender,
        String keyword,
        PhotoStyle photoStyle) {
}
