package com.dnd.jjigeojulge.reservation.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.reservation.application.dto.query.AppliedReservationListDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.CreatedReservationListDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationDetailDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSearchCondition;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSummaryDto;

public interface ReservationQueryRepository {

	/**
	 * 사전 예약 탐색 리스트 (동적 필터 및 페이징)
	 */
	Page<ReservationSummaryDto> searchReservations(ReservationSearchCondition condition, Long cursor, int limit);

	/**
	 * 내가 올린 동행 예약 리스트
	 */
	Page<CreatedReservationListDto> getMyCreatedReservations(Long ownerId, Long cursor, int limit);

	/**
	 * 내가 지원한 동행 예약 리스트 (Applicant 기준)
	 */
	Page<AppliedReservationListDto> getMyAppliedReservations(Long applicantId, Long cursor, int limit);

	/**
	 * 예약 상세 화면 (사전 예약 탐색에서 진입 시)
	 */
	Optional<ReservationDetailDto> getReservationDetail(Long reservationId);

	/**
	 * 작성자(방장) 본인 확인용 가벼운 쿼리
	 */
	boolean existsByIdAndOwnerId(Long reservationId, Long ownerId);

	/**
	 * 지원자 전체 목록 조회 (User 조인 포함)
	 */
	com.dnd.jjigeojulge.reservation.application.dto.query.ApplicantListResponseDto getApplicants(Long reservationId);
}
