package com.dnd.jjigeojulge.presentation.user.data;

public record ProfileDto(
	String username,
	String profileImageUrl,
	String email,
	String phoneNumber
) {
}
