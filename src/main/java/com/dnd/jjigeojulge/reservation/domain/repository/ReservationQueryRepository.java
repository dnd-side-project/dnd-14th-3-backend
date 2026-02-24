package com.dnd.jjigeojulge.reservation.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dnd.jjigeojulge.reservation.application.dto.query.MyReservationDetailDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationDetailDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSearchCondition;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSummaryDto;

public interface ReservationQueryRepository {

	/**
	 * 사전 예약 탐색 리스트 (동적 필터 및 페이징)
	 */
	Page<ReservationSummaryDto> searchReservations(ReservationSearchCondition condition, Pageable pageable);

	/**
	 * 내 예약 대기 화면 (내 예약 상세 + 지원자 리스트)
	 */
	Optional<MyReservationDetailDto> getMyReservationDetail(Long reservationId, Long ownerId);

	/**
	 * 예약 상세 화면 (사전 예약 탐색에서 진입 시)
	 */
	Optional<ReservationDetailDto> getReservationDetail(Long reservationId);
}
