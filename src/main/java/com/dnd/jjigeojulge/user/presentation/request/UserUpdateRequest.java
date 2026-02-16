package com.dnd.jjigeojulge.user.presentation.request;

import java.util.Set;

import com.dnd.jjigeojulge.user.domain.StyleName;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.presentation.validation.Nickname;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
	@NotBlank(message = "사용자 이름은 비어 있을 수 없습니다")
	@Nickname
	String newUsername,
	@NotNull(message = "성별은 필수입니다")
	Gender gender,
	@NotNull
	Set<StyleName> preferredStyles
) {
}
