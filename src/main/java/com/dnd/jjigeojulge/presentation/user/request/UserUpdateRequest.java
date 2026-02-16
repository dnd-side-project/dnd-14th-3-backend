package com.dnd.jjigeojulge.presentation.user.request;

import java.util.Set;

import com.dnd.jjigeojulge.domain.common.StyleName;
import com.dnd.jjigeojulge.domain.user.Gender;
import com.dnd.jjigeojulge.presentation.user.validation.Nickname;

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
