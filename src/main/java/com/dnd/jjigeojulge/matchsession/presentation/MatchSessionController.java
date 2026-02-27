package com.dnd.jjigeojulge.matchsession.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails;
import com.dnd.jjigeojulge.global.common.response.ApiResponse;
import com.dnd.jjigeojulge.matchsession.application.MatchSessionService;
import com.dnd.jjigeojulge.matchsession.presentation.api.MatchSessionApi;
import com.dnd.jjigeojulge.matchsession.presentation.data.SessionDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/match-sessions")
public class MatchSessionController implements MatchSessionApi {

	private final MatchSessionService matchSessionService;

	@Override
	@GetMapping("{sessionId}")
	public ResponseEntity<ApiResponse<SessionDto>> getMatchSession(
		@PathVariable Long sessionId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = userDetails.id();
		SessionDto sessionDetail = matchSessionService.getSessionDetail(userId, sessionId);
		return ResponseEntity.ok(ApiResponse.success(sessionDetail));
	}
}
