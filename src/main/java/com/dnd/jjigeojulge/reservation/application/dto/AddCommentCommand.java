package com.dnd.jjigeojulge.reservation.application.dto;

public record AddCommentCommand(
        Long reservationId,
        Long authorId,
        String content
) {
}
