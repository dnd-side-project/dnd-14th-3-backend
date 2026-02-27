package com.dnd.jjigeojulge.auth.application.dto;

public sealed interface AuthResult permits AuthResult.Success, AuthResult.RegisterNeeded {

	record Success(
			String accessToken,
			String refreshToken) implements AuthResult {
	}

	record RegisterNeeded(
			String registerToken,
			String profileImageUrl) implements AuthResult {
	}
}
