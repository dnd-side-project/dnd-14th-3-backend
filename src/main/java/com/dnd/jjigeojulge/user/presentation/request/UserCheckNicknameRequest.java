package com.dnd.jjigeojulge.user.presentation.request;

import com.dnd.jjigeojulge.user.presentation.validation.Nickname;

import jakarta.validation.constraints.NotBlank;

public record UserCheckNicknameRequest(
	@NotBlank
	@Nickname
	String nickname
) {
}
