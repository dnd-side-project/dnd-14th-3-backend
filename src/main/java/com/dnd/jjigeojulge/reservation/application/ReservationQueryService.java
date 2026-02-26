package com.dnd.jjigeojulge.reservation.application;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.reservation.application.dto.query.AppliedReservationListDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.CreatedReservationListDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationCommentDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationDetailDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationListResponseDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSearchCondition;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSummaryDto;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationCommentQueryRepository;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationQueryRepository reservationQueryRepository;
    private final ReservationCommentQueryRepository reservationCommentQueryRepository;

    public Page<ReservationListResponseDto> searchReservations(ReservationSearchCondition condition,
            Long cursor, int limit) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return reservationQueryRepository.searchReservations(condition, cursor, limit)
                .map(dto -> ReservationListResponseDto.of(dto, now));
    }

    public Page<CreatedReservationListDto> getMyCreatedReservations(Long ownerId, Long cursor, int limit) {
        return reservationQueryRepository.getMyCreatedReservations(ownerId, cursor, limit);
    }

    public Page<AppliedReservationListDto> getMyAppliedReservations(Long applicantId, Long cursor, int limit) {
        return reservationQueryRepository.getMyAppliedReservations(applicantId, cursor, limit);
    }

    public ReservationDetailDto getReservationDetail(Long reservationId) {
        return reservationQueryRepository.getReservationDetail(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 정보를 찾을 수 없습니다."));
    }

    public Page<ReservationCommentDto> getReservationComments(Long reservationId, Long cursor, int limit) {
        return reservationCommentQueryRepository.getReservationComments(reservationId, cursor, limit);
    }

    public com.dnd.jjigeojulge.reservation.application.dto.query.ApplicantListResponseDto getApplicants(
            Long reservationId, Long currentUserId) {
        // 방장 권한(Ownership) 검증 (Fast-Fail)
        boolean isOwner = reservationQueryRepository.existsByIdAndOwnerId(reservationId, currentUserId);
        if (!isOwner) {
            throw new com.dnd.jjigeojulge.reservation.domain.exception.ReservationAccessDeniedException();
        }

        return reservationQueryRepository.getApplicants(reservationId);
    }
}
