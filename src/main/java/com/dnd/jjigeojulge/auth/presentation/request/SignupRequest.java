package com.dnd.jjigeojulge.auth.presentation.request;

import java.util.List;
import com.dnd.jjigeojulge.auth.application.dto.SignupCommand;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.StyleName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SignupRequest(
	@NotBlank(message = "닉네임은 필수입니다.")
	String nickname,

	@NotNull(message = "성별은 필수입니다.")
	Gender gender,

	String profileImageUrl,

	@NotEmpty(message = "촬영 스타일은 최소 1개 이상 선택해야 합니다.")
	List<StyleName> photoStyles
) {
	public SignupCommand toCommand(String registerToken) {
		return new SignupCommand(registerToken, nickname, gender, profileImageUrl, photoStyles);
	}
}
