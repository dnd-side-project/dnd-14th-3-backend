package com.dnd.jjigeojulge.user.presentation.request;

import com.dnd.jjigeojulge.user.presentation.validation.Nickname;

import jakarta.validation.constraints.NotBlank;

public record UserCheckNicknameRequest(
	@NotBlank(message = "공백일 수 없습니다.")
	@Nickname
	String nickname
) {
}
