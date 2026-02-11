package com.dnd.jjigeojulge.presentation.user.data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ConsentDto(
	boolean notificationAllowed,
	boolean locationAllowed,
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime updatedAt
) {
}
