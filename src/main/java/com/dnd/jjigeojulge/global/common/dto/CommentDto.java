package com.dnd.jjigeojulge.global.common.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 DTO")
public record CommentDto(
	Long commentId,
	@Schema(description = "댓글 메시지", example = "저도 동행 참여하고 싶어요!")
	String content,
	@Schema(description = "댓글 작성자")
	AuthorDto author,
	@Schema(description = "댓글 생성 시간 (KST, ISO-8601)", example = "2026-02-16T02:10:00")
	LocalDateTime createdAt
) {
}
