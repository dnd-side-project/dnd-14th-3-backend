package com.dnd.jjigeojulge.sse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sse")
public class SseController {

	private final SseService sseService;

	@GetMapping(value = {"{userId}"}, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe(
		@PathVariable Long userId
		// @AuthenticationPrincipal CustomUserDetails userDetails  // 로그인 기능 완성 or 로컬 실행 가능 후 수정
	) {
		// Long userId = userDetails.id();
		return sseService.connect(userId);
	}
}
