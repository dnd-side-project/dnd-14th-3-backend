package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;

public record ReservationCommentDto(
        Long commentId,
        Long reservationId,
        Long authorId,
        String authorNickname,
        String authorProfileImageUrl,
        String content,
        boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
