package com.dnd.jjigeojulge.reservation.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;

import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationCommentDto;
import com.dnd.jjigeojulge.reservation.domain.ReservationComment;

public interface ReservationCommentQueryRepository {
    List<ReservationComment> findAllByReservationId(Long reservationId);

    Page<ReservationCommentDto> getReservationComments(Long reservationId, Long cursor, int limit);
}
