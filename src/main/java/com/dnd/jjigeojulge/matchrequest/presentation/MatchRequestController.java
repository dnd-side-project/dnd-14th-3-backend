package com.dnd.jjigeojulge.matchrequest.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.jjigeojulge.global.common.response.ApiResponse;
import com.dnd.jjigeojulge.matchrequest.application.MatchRequestService;
import com.dnd.jjigeojulge.matchrequest.presentation.api.MatchRequestApi;
import com.dnd.jjigeojulge.matchrequest.presentation.data.MatchRequestDto;
import com.dnd.jjigeojulge.matchrequest.presentation.request.MatchRequestCreateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/match-requests")
public class MatchRequestController implements MatchRequestApi {

	private final MatchRequestService matchRequestService;

	@Override
	@PostMapping
	public ResponseEntity<ApiResponse<MatchRequestDto>> create(@RequestBody @Valid MatchRequestCreateRequest request) {
		MatchRequestDto matchRequestDto = matchRequestService.create(1L, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(matchRequestDto));
	}

	@Override
	@DeleteMapping("{matchRequestId}")
	public ResponseEntity<Void> cancel(@PathVariable Long matchRequestId) {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Override
	@GetMapping("{matchRequestId}")
	public ResponseEntity<ApiResponse<MatchRequestDto>> get(@PathVariable Long matchRequestId) {
		return ResponseEntity.ok(ApiResponse.success(null));
	}
}
