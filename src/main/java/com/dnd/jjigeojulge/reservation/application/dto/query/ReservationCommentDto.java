package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "동행 예약글 하단 댓글 응답 객체")
public record ReservationCommentDto(
                @Schema(description = "댓글 ID (페이징 커서용)", example = "500") Long commentId,
                @Schema(description = "예약글 ID", example = "100") Long reservationId,
                @Schema(description = "댓글 작성자 ID", example = "2") Long authorId,
                @Schema(description = "작성자 닉네임", example = "라이언") String authorNickname,
                @Schema(description = "작성자 프로필 섬네일", example = "https://image.url/lion.png") String authorProfileImageUrl,
                @Schema(description = "댓글 내용 본문", example = "저 지원했는데 확인 부탁드려요!") String content,
                @Schema(description = "삭제 여부 플래그 (true면 '삭제된 댓글입니다' 처리용)", example = "false") boolean isDeleted,
                @Schema(description = "댓글 최초 작성일시", example = "2026-02-25T10:00:00") LocalDateTime createdAt,
                @Schema(description = "댓글 최후 수정일시", example = "2026-02-25T10:00:00") LocalDateTime updatedAt) {
}
