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
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationApi {

	private final com.dnd.jjigeojulge.reservation.application.ReservationService reservationService;
	private final com.dnd.jjigeojulge.reservation.application.ReservationQueryService reservationQueryService;

	@Override
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<com.dnd.jjigeojulge.reservation.application.dto.query.ReservationListResponseDto>>> getList(
			@RequestParam(value = "cursor", required = false) Long cursor,
			@RequestParam(defaultValue = "10") int limit) {
		com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSearchCondition condition = com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSearchCondition
				.builder().build(); // TODO: 검색 필터 추가 시 바인딩
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
			@AuthenticationPrincipal Long currentUserId,
			@RequestBody @Valid ReservationCreateRequest request) {
		// (임시) 현재는 Long 필드로만 받도록 되어 있으나 나중에 Principal Details 등으로 변경될 수 있음
		if (currentUserId == null) {
			currentUserId = 1L; // fallback for current test setup if missing
		}
		com.dnd.jjigeojulge.reservation.application.dto.CreateReservationCommand command = new com.dnd.jjigeojulge.reservation.application.dto.CreateReservationCommand(
				currentUserId,
				request.title(),
				request.region1Depth(),
				request.specificPlace(),
				request.location().latitude(),
				request.location().longitude(),
				request.scheduledAt(),
				request.shootingDuration(),
				request.requestMessage());
		Long reservationId = reservationService.createReservation(command);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(reservationId));
	}

	@Override
	@org.springframework.web.bind.annotation.PatchMapping("/{reservationId}")
	public ResponseEntity<ApiResponse<Void>> update(
			@PathVariable Long reservationId,
			@AuthenticationPrincipal Long currentUserId,
			@RequestBody @Valid com.dnd.jjigeojulge.reservation.presentation.request.ReservationUpdateRequest request) {
		Long activeUserId = currentUserId != null ? currentUserId : 1L; // Fallback for dev
		reservationService.updateReservation(
				com.dnd.jjigeojulge.reservation.application.dto.UpdateReservationCommand.of(reservationId, activeUserId,
						request));
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.DeleteMapping("/{reservationId}")
	public ResponseEntity<ApiResponse<Void>> cancel(
			@PathVariable Long reservationId,
			@AuthenticationPrincipal Long currentUserId) {
		Long activeUserId = currentUserId != null ? currentUserId : 1L;
		reservationService.cancelReservation(reservationId, activeUserId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.PostMapping("/{reservationId}/apply")
	public ResponseEntity<ApiResponse<Void>> apply(
			@PathVariable Long reservationId,
			@AuthenticationPrincipal Long currentUserId) {
		Long activeUserId = currentUserId != null ? currentUserId : 2L; // 게스트용 Mock
		reservationService.applyToReservation(reservationId, activeUserId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.DeleteMapping("/{reservationId}/apply")
	public ResponseEntity<ApiResponse<Void>> cancelApplication(
			@PathVariable Long reservationId,
			@AuthenticationPrincipal Long currentUserId) {
		Long activeUserId = currentUserId != null ? currentUserId : 2L;
		reservationService.cancelApplicationToReservation(reservationId, activeUserId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.PostMapping("/{reservationId}/applicants/{applicantId}/accept")
	public ResponseEntity<ApiResponse<Void>> acceptApplicant(
			@PathVariable Long reservationId,
			@PathVariable Long applicantId,
			@AuthenticationPrincipal Long currentUserId) {
		Long activeUserId = currentUserId != null ? currentUserId : 1L;
		reservationService.acceptApplicant(reservationId, activeUserId, applicantId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.PostMapping("/{reservationId}/applicants/{applicantId}/reject")
	public ResponseEntity<ApiResponse<Void>> rejectApplicant(
			@PathVariable Long reservationId,
			@PathVariable Long applicantId,
			@AuthenticationPrincipal Long currentUserId) {
		Long activeUserId = currentUserId != null ? currentUserId : 1L;
		reservationService.rejectApplicant(reservationId, activeUserId, applicantId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@org.springframework.web.bind.annotation.PostMapping("/{reservationId}/complete")
	public ResponseEntity<ApiResponse<Void>> complete(
			@PathVariable Long reservationId,
			@AuthenticationPrincipal Long currentUserId) {
		// 완료 처리 시에도 방장인지 검증하는 로직이 도메인에 있다면 principal을 넘겨야 함 (현재는 reservationId만 요구중이나
		// 명시적으로 둠)
		reservationService.completeReservation(reservationId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@GetMapping("/created")
	public ResponseEntity<ApiResponse<PageResponse<CreatedReservationListDto>>> getMyCreatedReservations(
			@AuthenticationPrincipal Long currentUserId,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = "10") int limit) {
		Long mockUserId = currentUserId != null ? currentUserId : 1L;
		return ResponseEntity.ok(ApiResponse.success(
				PageResponse.from(reservationQueryService.getMyCreatedReservations(mockUserId, cursor, limit))));
	}

	@Override
	@GetMapping("/applied")
	public ResponseEntity<ApiResponse<PageResponse<AppliedReservationListDto>>> getMyAppliedReservations(
			@AuthenticationPrincipal Long currentUserId,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = "10") int limit) {
		Long mockUserId = currentUserId != null ? currentUserId : 2L;
		return ResponseEntity.ok(ApiResponse.success(
				PageResponse.from(reservationQueryService.getMyAppliedReservations(mockUserId, cursor, limit))));
	}

	@Override
	@GetMapping("/{reservationId}/comments")
	public ResponseEntity<ApiResponse<PageResponse<ReservationCommentDto>>> getComments(
			@PathVariable Long reservationId,
			@RequestParam(value = "cursor", required = false) Long cursor,
			@RequestParam(defaultValue = "10") int limit) {
		return ResponseEntity.ok(ApiResponse.success(
				PageResponse.from(reservationQueryService.getReservationComments(reservationId, cursor, limit))));
	}

	@Override
	@GetMapping("/{reservationId}/applicants")
	public ResponseEntity<ApiResponse<com.dnd.jjigeojulge.reservation.application.dto.query.ApplicantListResponseDto>> getApplicants(
			@AuthenticationPrincipal Long currentUserId, // TODO: 시큐리티 설정 후 @AuthenticationPrincipal 등으로 교체
			@PathVariable Long reservationId) {
		// 현재 인증 생략 상태이므로 임시로 1L(방장) 고정 (테스트용)
		Long mockUserId = currentUserId != null ? currentUserId : 1L;
		return ResponseEntity.ok(ApiResponse.success(
				reservationQueryService.getApplicants(reservationId, mockUserId)));
	}
}
