package com.dnd.jjigeojulge.auth.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
		boolean isNewUser,
		TokenResponse tokens,
		String registerToken,
		String profileImageUrl) {
	public static LoginResponse loginSuccess(TokenResponse tokens) {
		return new LoginResponse(false, tokens, null, null);
	}

	public static LoginResponse registerNeeded(String registerToken, String profileImageUrl) {
		return new LoginResponse(true, null, registerToken, profileImageUrl);
	}
}
