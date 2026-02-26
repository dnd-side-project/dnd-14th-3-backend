package com.dnd.jjigeojulge.user.presentation.request;

import java.util.Set;

import com.dnd.jjigeojulge.user.domain.AgeGroup;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.StyleName;
import com.dnd.jjigeojulge.user.presentation.validation.Nickname;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
		@NotBlank(message = "사용자 이름은 비어 있을 수 없습니다") @Nickname String newUsername,
		@NotNull(message = "성별은 필수입니다") Gender gender,
		@NotNull(message = "연령대는 필수입니다") AgeGroup ageGroup,
		@Size(max = 50, message = "한줄 소개는 50자를 초과할 수 없습니다.") String introduction,
		@NotEmpty(message = "촬영 스타일은 최소 1개 이상 선택해야 합니다.") Set<StyleName> preferredStyles) {
}
