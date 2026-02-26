package com.dnd.jjigeojulge.reservation.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.jjigeojulge.global.common.response.ApiResponse;
import com.dnd.jjigeojulge.global.common.response.PageResponse;
import com.dnd.jjigeojulge.reservation.application.dto.query.AppliedReservationListDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.CreatedReservationListDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationCommentDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationDetailDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSummaryDto;
import com.dnd.jjigeojulge.reservation.presentation.api.ReservationApi;
import com.dnd.jjigeojulge.reservation.presentation.data.ReservationDto;
import com.dnd.jjigeojulge.reservation.presentation.request.ReservationCreateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationApi {

	private final com.dnd.jjigeojulge.reservation.application.ReservationService reservationService;

	@Override
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<ReservationSummaryDto>>> getList(
			@RequestParam(value = "cursor", required = false) Long cursor,
			@RequestParam(defaultValue = "10") int limit) {
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@GetMapping("/{reservationId}")
	public ResponseEntity<ApiResponse<ReservationDetailDto>> getDetail(@PathVariable Long reservationId) {
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@PostMapping
	public ResponseEntity<ApiResponse<ReservationDto>> create(@RequestBody @Valid ReservationCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.PatchMapping("/{reservationId}")
	public ResponseEntity<ApiResponse<ReservationDto>> update(
			@PathVariable Long reservationId,
			@RequestBody @Valid com.dnd.jjigeojulge.reservation.presentation.request.ReservationUpdateRequest request) {
		// TODO: 시큐리티 컨텍스트에서 인증된 사용자의 ID를 가져와야 합니다. 현재 테스트용 1L 고정.
		Long currentUserId = 1L;
		reservationService.updateReservation(
				com.dnd.jjigeojulge.reservation.application.dto.UpdateReservationCommand.of(reservationId,
						currentUserId, request));
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.DeleteMapping("/{reservationId}")
	public ResponseEntity<ApiResponse<Void>> cancel(
			@PathVariable Long reservationId) {
		// TODO: 시큐리티 컨텍스트 적용 시 수정
		Long currentUserId = 1L;
		reservationService.cancelReservation(reservationId, currentUserId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.PostMapping("/{reservationId}/apply")
	public ResponseEntity<ApiResponse<Void>> apply(
			@PathVariable Long reservationId) {
		// TODO: 시큐리티 컨텍스트 적용 시 수정
		Long currentUserId = 2L; // 게스트용 Mock
		reservationService.applyToReservation(reservationId, currentUserId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.DeleteMapping("/{reservationId}/apply")
	public ResponseEntity<ApiResponse<Void>> cancelApplication(
			@PathVariable Long reservationId) {
		// TODO: 시큐리티 컨텍스트 적용 시 수정
		Long currentUserId = 2L; // 게스트용 Mock
		reservationService.cancelApplicationToReservation(reservationId, currentUserId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.PostMapping("/{reservationId}/applicants/{applicantId}/accept")
	public ResponseEntity<ApiResponse<Void>> acceptApplicant(
			@PathVariable Long reservationId,
			@PathVariable Long applicantId) {
		// TODO: 시큐리티 컨텍스트 적용 시 수정
		Long currentUserId = 1L; // 방장
		reservationService.acceptApplicant(reservationId, currentUserId, applicantId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.PostMapping("/{reservationId}/applicants/{applicantId}/reject")
	public ResponseEntity<ApiResponse<Void>> rejectApplicant(
			@PathVariable Long reservationId,
			@PathVariable Long applicantId) {
		// TODO: 시큐리티 컨텍스트 적용 시 수정
		Long currentUserId = 1L; // 방장
		reservationService.rejectApplicant(reservationId, currentUserId, applicantId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.PostMapping("/{reservationId}/complete")
	public ResponseEntity<ApiResponse<Void>> complete(
			@PathVariable Long reservationId) {
		reservationService.completeReservation(reservationId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@GetMapping("/created")
	public ResponseEntity<ApiResponse<PageResponse<CreatedReservationListDto>>> getMyCreatedReservations(
			Long currentUserId,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = "10") int limit) {
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@GetMapping("/applied")
	public ResponseEntity<ApiResponse<PageResponse<AppliedReservationListDto>>> getMyAppliedReservations(
			Long currentUserId,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = "10") int limit) {
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@GetMapping("/{reservationId}/comments")
	public ResponseEntity<ApiResponse<PageResponse<ReservationCommentDto>>> getComments(
			@PathVariable Long reservationId,
			@RequestParam(value = "cursor", required = false) Long cursor,
			@RequestParam(defaultValue = "10") int limit) {
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@GetMapping("/{reservationId}/applicants")
	public ResponseEntity<ApiResponse<com.dnd.jjigeojulge.reservation.application.dto.query.ApplicantListResponseDto>> getApplicants(
			Long currentUserId, // TODO: 시큐리티 설정 후 @AuthenticationPrincipal 등으로 교체
			@PathVariable Long reservationId) {
		// 현재 인증 생략 상태이므로 임시로 1L(방장) 고정 (테스트용)
		Long mockUserId = currentUserId != null ? currentUserId : 1L;
		return ResponseEntity.ok(ApiResponse.success(
				new com.dnd.jjigeojulge.reservation.application.ReservationQueryService(null, null)
						.getApplicants(reservationId, mockUserId) // TODO: 제대로 주입된 빈 사용
		));
	}
}
