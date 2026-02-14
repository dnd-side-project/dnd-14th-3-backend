package com.dnd.jjigeojulge.presentation.user.data;

import java.util.List;

import com.dnd.jjigeojulge.domain.photostyle.PhotoStyle;
import com.dnd.jjigeojulge.domain.photostyle.StyleName;
import com.dnd.jjigeojulge.domain.user.Gender;
import com.dnd.jjigeojulge.domain.user.User;
import com.dnd.jjigeojulge.domain.user.UserPhotoStyle;
import com.dnd.jjigeojulge.domain.user.UserSetting;

public record ProfileDto(
	String nickname,
	Gender gender,
	String profileImageUrl,
	String email,
	String phoneNumber,
	List<StyleName> photoStyles,
	ConsentDto consent
) {

	public static ProfileDto from(User user) {
		List<StyleName> list = user.getPhotoStyles().stream()
			.map(UserPhotoStyle::getPhotoStyle)
			.map(PhotoStyle::getName)
			.toList();
		UserSetting userSetting = user.getUserSetting();

		return new ProfileDto(
			user.getNickname(),
			user.getGender(),
			user.getProfileImageUrl(),
			user.getKakaoUserEmail(),
			user.getPhoneNumber(),
			list,
			ConsentDto.from(userSetting)
		);
	}
}
