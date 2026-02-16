package com.dnd.jjigeojulge.presentation.reseration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.jjigeojulge.presentation.common.response.ApiResponse;
import com.dnd.jjigeojulge.presentation.common.response.PageResponse;
import com.dnd.jjigeojulge.presentation.reseration.api.ReservationApi;
import com.dnd.jjigeojulge.presentation.reseration.data.ReservationDto;
import com.dnd.jjigeojulge.presentation.reseration.request.ReservationCreateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationApi {

	@Override
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<ReservationDto>>> getList(
		@RequestParam(value = "cursor", required = false) Long cursor,
		@RequestParam(defaultValue = "10") int limit
	) {
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@GetMapping("/{reservationId}")
	public ResponseEntity<ApiResponse<ReservationDto>> getDetail(@PathVariable Long reservationId) {
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@PostMapping
	public ResponseEntity<ApiResponse<ReservationDto>> create(@RequestBody @Valid ReservationCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
	}
}
