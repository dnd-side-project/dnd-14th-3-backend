package com.dnd.jjigeojulge.reservation.infra;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dnd.jjigeojulge.reservation.domain.ReservationComment;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationCommentRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationCommentRepositoryImpl implements ReservationCommentRepository {

    private final JpaReservationCommentRepository jpaReservationCommentRepository;

    @Override
    public ReservationComment save(ReservationComment comment) {
        return jpaReservationCommentRepository.save(comment);
    }

    @Override
    public Optional<ReservationComment> findById(Long id) {
        return jpaReservationCommentRepository.findById(id);
    }

    @Override
    public void delete(ReservationComment comment) {
        jpaReservationCommentRepository.delete(comment);
    }
}
