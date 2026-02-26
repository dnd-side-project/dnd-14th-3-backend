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
import com.dnd.jjigeojulge.reservation.presentation.api.ReservationApi;
import com.dnd.jjigeojulge.reservation.presentation.request.ReservationCreateRequest;
import com.dnd.jjigeojulge.reservation.application.ReservationService;
import com.dnd.jjigeojulge.reservation.application.ReservationQueryService;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationListResponseDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSearchCondition;
import com.dnd.jjigeojulge.reservation.application.dto.query.ApplicantListResponseDto;
import com.dnd.jjigeojulge.reservation.presentation.request.ReservationUpdateRequest;
import com.dnd.jjigeojulge.global.annotation.CurrentUserId;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationApi {

	private final ReservationService reservationService;
	private final ReservationQueryService reservationQueryService;

	@Override
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<ReservationListResponseDto>>> getList(
			@org.springframework.web.bind.annotation.ModelAttribute ReservationSearchCondition condition,
			@RequestParam(value = "cursor", required = false) Long cursor,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
		return ResponseEntity.ok(ApiResponse.success(
				PageResponse.from(reservationQueryService.searchReservations(condition, cursor, limit))));
	}

	@Override
	@GetMapping("/{reservationId}")
	public ResponseEntity<ApiResponse<ReservationDetailDto>> getDetail(@PathVariable Long reservationId) {
		return ResponseEntity.ok(ApiResponse.success(reservationQueryService.getReservationDetail(reservationId)));
	}

	@Override
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> create(
			@CurrentUserId Long currentUserId,
			@RequestBody @Valid ReservationCreateRequest request) {
		Long reservationId = reservationService.createReservation(request.toCommand(currentUserId));
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(reservationId));
	}

	@Override
	@org.springframework.web.bind.annotation.PatchMapping("/{reservationId}")
	public ResponseEntity<ApiResponse<Void>> update(
			@PathVariable Long reservationId,
			@CurrentUserId Long currentUserId,
			@RequestBody @Valid ReservationUpdateRequest request) {
		reservationService.updateReservation(request.toCommand(reservationId, currentUserId));
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.DeleteMapping("/{reservationId}")
	public ResponseEntity<ApiResponse<Void>> cancel(
			@PathVariable Long reservationId,
			@CurrentUserId Long currentUserId) {
		reservationService.cancelReservation(reservationId, currentUserId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.PostMapping("/{reservationId}/apply")
	public ResponseEntity<ApiResponse<Void>> apply(
			@PathVariable Long reservationId,
			@CurrentUserId Long currentUserId) {
		reservationService.applyToReservation(reservationId, currentUserId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.DeleteMapping("/{reservationId}/apply")
	public ResponseEntity<ApiResponse<Void>> cancelApplication(
			@PathVariable Long reservationId,
			@CurrentUserId Long currentUserId) {
		reservationService.cancelApplicationToReservation(reservationId, currentUserId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.PostMapping("/{reservationId}/applicants/{applicantId}/accept")
	public ResponseEntity<ApiResponse<Void>> acceptApplicant(
			@PathVariable Long reservationId,
			@PathVariable Long applicantId,
			@CurrentUserId Long currentUserId) {
		reservationService.acceptApplicant(reservationId, currentUserId, applicantId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.PostMapping("/{reservationId}/applicants/{applicantId}/reject")
	public ResponseEntity<ApiResponse<Void>> rejectApplicant(
			@PathVariable Long reservationId,
			@PathVariable Long applicantId,
			@CurrentUserId Long currentUserId) {
		reservationService.rejectApplicant(reservationId, currentUserId, applicantId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@GetMapping("/created")
	public ResponseEntity<ApiResponse<PageResponse<CreatedReservationListDto>>> getMyCreatedReservations(
			@CurrentUserId Long currentUserId,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
		return ResponseEntity.ok(ApiResponse.success(
				PageResponse.from(reservationQueryService.getMyCreatedReservations(currentUserId, cursor, limit))));
	}

	@Override
	@GetMapping("/applied")
	public ResponseEntity<ApiResponse<PageResponse<AppliedReservationListDto>>> getMyAppliedReservations(
			@CurrentUserId Long currentUserId,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
		return ResponseEntity.ok(ApiResponse.success(
				PageResponse.from(reservationQueryService.getMyAppliedReservations(currentUserId, cursor, limit))));
	}

	@Override
	@GetMapping("/{reservationId}/comments")
	public ResponseEntity<ApiResponse<PageResponse<ReservationCommentDto>>> getComments(
			@PathVariable Long reservationId,
			@RequestParam(value = "cursor", required = false) Long cursor,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
		return ResponseEntity.ok(ApiResponse.success(
				PageResponse.from(reservationQueryService.getReservationComments(reservationId, cursor, limit))));
	}

	@Override
	@GetMapping("/{reservationId}/applicants")
	public ResponseEntity<ApiResponse<ApplicantListResponseDto>> getApplicants(
			@CurrentUserId Long currentUserId,
			@PathVariable Long reservationId) {
		return ResponseEntity.ok(ApiResponse.success(
				reservationQueryService.getApplicants(reservationId, currentUserId)));
	}
}
