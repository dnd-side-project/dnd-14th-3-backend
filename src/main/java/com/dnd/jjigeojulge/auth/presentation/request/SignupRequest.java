package com.dnd.jjigeojulge.auth.presentation.request;

import java.util.List;

import com.dnd.jjigeojulge.auth.application.dto.SignupCommand;
import com.dnd.jjigeojulge.user.domain.AgeGroup;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.StyleName;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignupRequest(
		@NotBlank(message = "닉네임은 필수입니다.") String nickname,

		@NotNull(message = "성별은 필수입니다.") Gender gender,

		@NotNull(message = "연령대는 필수입니다.") AgeGroup ageGroup,

		@Size(max = 50, message = "한줄 소개는 50자를 초과할 수 없습니다.") String introduction,

		String profileImageUrl,

		@NotEmpty(message = "촬영 스타일은 최소 1개 이상 선택해야 합니다.") List<StyleName> photoStyles) {
	public SignupCommand toCommand(String registerToken) {
		return new SignupCommand(registerToken, nickname, gender, ageGroup, introduction, profileImageUrl, photoStyles);
	}
}
