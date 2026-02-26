package com.dnd.jjigeojulge.user.presentation.data;

import java.util.List;

import com.dnd.jjigeojulge.user.domain.AgeGroup;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.PhotoStyle;
import com.dnd.jjigeojulge.user.domain.StyleName;
import com.dnd.jjigeojulge.user.domain.User;
import com.dnd.jjigeojulge.user.domain.UserPhotoStyle;
import com.dnd.jjigeojulge.user.domain.UserSetting;

public record ProfileDto(
		Long userId,
		String nickname,
		Gender gender,
		AgeGroup ageGroup,
		String introduction,
		String profileImageUrl,
		List<StyleName> photoStyles,
		ConsentDto consent) {

	public static ProfileDto from(User user) {
		List<StyleName> list = user.getPhotoStyles().stream()
				.map(UserPhotoStyle::getPhotoStyle)
				.map(PhotoStyle::getName)
				.toList();
		UserSetting userSetting = user.getUserSetting();

		return new ProfileDto(
				user.getId(),
				user.getNickname(),
				user.getGender(),
				user.getAgeGroup(),
				user.getIntroduction() != null ? user.getIntroduction().getValue() : null,
				user.getProfileImageUrl(),
				list,
				userSetting != null ? ConsentDto.from(userSetting) : null);
	}
}
