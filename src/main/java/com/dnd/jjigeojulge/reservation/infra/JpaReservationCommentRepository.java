package com.dnd.jjigeojulge.reservation.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.jjigeojulge.reservation.domain.ReservationComment;

public interface JpaReservationCommentRepository extends JpaRepository<ReservationComment, Long> {
    List<ReservationComment> findAllByReservationIdOrderByCreatedAtAsc(Long reservationId);
}
