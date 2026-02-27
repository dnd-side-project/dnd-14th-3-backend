package com.dnd.jjigeojulge.reservation.domain.repository;

import java.util.Optional;

import com.dnd.jjigeojulge.reservation.domain.ReservationComment;

public interface ReservationCommentRepository {
    ReservationComment save(ReservationComment comment);

    Optional<ReservationComment> findById(Long id);

    void delete(ReservationComment comment);
}
