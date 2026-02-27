package com.dnd.jjigeojulge.websocket.data;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "매칭 세션 실시간 이벤트 응답")
public record MatchSessionMessageDto<T>(
	@Schema(description = "이벤트 타입")
	MatchSessionMessageType type,

	@Schema(description = "세션 ID")
	Long sessionId,

	@Schema(description = "보내는 사람 ID", nullable = true)
	Long senderId,

	@Schema(description = "발생 시각")
	LocalDateTime timestamp,

	@Schema(description = "이벤트 상세 데이터")
	T data
) {
	public static <T> MatchSessionMessageDto<T> of
		(MatchSessionMessageType type,
			Long sessionId,
			Long senderId,
			T data
		) {
		return new MatchSessionMessageDto<>(type, sessionId, senderId, LocalDateTime.now(), data);
	}
}
