package com.dnd.jjigeojulge.presentation.user.request;

import com.dnd.jjigeojulge.presentation.user.validation.Nickname;

import jakarta.validation.constraints.NotBlank;

public record UserCheckNicknameRequest(
	@NotBlank
	@Nickname
	String nickname
) {
}
