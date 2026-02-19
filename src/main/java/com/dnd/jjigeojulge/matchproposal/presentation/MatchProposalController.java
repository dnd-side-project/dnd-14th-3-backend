package com.dnd.jjigeojulge.matchproposal.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails;
import com.dnd.jjigeojulge.global.common.response.ApiResponse;
import com.dnd.jjigeojulge.matchproposal.application.MatchProposalService;
import com.dnd.jjigeojulge.matchproposal.data.MatchProposalDto;
import com.dnd.jjigeojulge.matchproposal.presentation.api.MatchProposalApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/match-proposals")
public class MatchProposalController implements MatchProposalApi {

	private final MatchProposalService matchProposalService;

	@Override
	@PostMapping("{proposalId}/accept")
	public ResponseEntity<ApiResponse<MatchProposalDto>> accept(
		@PathVariable Long proposalId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		MatchProposalDto dto = matchProposalService.accept(userDetails.id(), proposalId);
		return ResponseEntity.ok(ApiResponse.success(dto));
	}

	@PostMapping("{proposalId}/reject")
	@Override
	public ResponseEntity<ApiResponse<MatchProposalDto>> reject(
		@PathVariable Long proposalId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		MatchProposalDto dto = matchProposalService.reject(userDetails.id(), proposalId);
		return ResponseEntity.ok(ApiResponse.success(dto));
	}
}
