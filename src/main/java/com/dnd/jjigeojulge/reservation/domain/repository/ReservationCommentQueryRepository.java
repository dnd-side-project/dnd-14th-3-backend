package com.dnd.jjigeojulge.reservation.domain.repository;

import java.util.List;

import com.dnd.jjigeojulge.reservation.domain.ReservationComment;

public interface ReservationCommentQueryRepository {
    // 상세 조회나 필터링된 목록 조회 등 조회 전용 로직이 여기에 추가됩니다.
    List<ReservationComment> findAllByReservationId(Long reservationId);
}
