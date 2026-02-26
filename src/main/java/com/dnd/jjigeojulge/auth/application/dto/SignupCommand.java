package com.dnd.jjigeojulge.auth.application.dto;

import java.util.List;

import com.dnd.jjigeojulge.user.domain.AgeGroup;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.StyleName;

public record SignupCommand(
		String registerToken,
		String nickname,
		Gender gender,
		AgeGroup ageGroup,
		String introduction,
		String profileImageUrl,
		List<StyleName> photoStyles) {
}
