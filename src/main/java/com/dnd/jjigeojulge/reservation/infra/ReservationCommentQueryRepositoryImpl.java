package com.dnd.jjigeojulge.reservation.infra;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.dnd.jjigeojulge.reservation.domain.ReservationComment;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationCommentQueryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationCommentQueryRepositoryImpl implements ReservationCommentQueryRepository {

    private final JpaReservationCommentRepository jpaReservationCommentRepository;

    @Override
    public List<ReservationComment> findAllByReservationId(Long reservationId) {
        // 우선은 JpaRepository의 기능을 활용하고, 추후 QueryDSL 등으로 고도화 가능합니다.
        return jpaReservationCommentRepository.findAllByReservationIdOrderByCreatedAtAsc(reservationId);
    }
}
