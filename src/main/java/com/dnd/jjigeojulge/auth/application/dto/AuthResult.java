package com.dnd.jjigeojulge.auth.application.dto;

public record AuthResult(
	boolean isNewUser,
	String accessToken,
	String refreshToken,
	String registerToken
) {
	public static AuthResult success(String accessToken, String refreshToken) {
		return new AuthResult(false, accessToken, refreshToken, null);
	}

	public static AuthResult registerNeeded(String registerToken) {
		return new AuthResult(true, null, null, registerToken);
	}
}
