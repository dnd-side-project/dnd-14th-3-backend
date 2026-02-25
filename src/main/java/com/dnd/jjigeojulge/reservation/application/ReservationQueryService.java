package com.dnd.jjigeojulge.reservation.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.reservation.application.dto.query.MyReservationDetailDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationDetailDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationListResponseDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSearchCondition;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSummaryDto;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationQueryRepository reservationQueryRepository;

    public Page<ReservationListResponseDto> searchReservations(ReservationSearchCondition condition,
            Pageable pageable) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return reservationQueryRepository.searchReservations(condition, pageable)
                .map(dto -> ReservationListResponseDto.of(dto, now));
    }

    public MyReservationDetailDto getMyReservationDetail(Long reservationId, Long ownerId) {
        return reservationQueryRepository.getMyReservationDetail(reservationId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없거나 접근 권한이 없습니다."));
    }

    public ReservationDetailDto getReservationDetail(Long reservationId) {
        return reservationQueryRepository.getReservationDetail(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 정보를 찾을 수 없습니다."));
    }
}
