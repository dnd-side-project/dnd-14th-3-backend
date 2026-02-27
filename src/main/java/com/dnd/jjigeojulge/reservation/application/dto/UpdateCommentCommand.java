package com.dnd.jjigeojulge.reservation.application.dto;

public record UpdateCommentCommand(
        Long commentId,
        Long userId,
        String content
) {
}
