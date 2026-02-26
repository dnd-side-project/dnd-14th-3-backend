package com.dnd.jjigeojulge.user.presentation.data;

import java.util.List;

import com.dnd.jjigeojulge.user.domain.AgeGroup;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.PhotoStyle;
import com.dnd.jjigeojulge.user.domain.StyleName;
import com.dnd.jjigeojulge.user.domain.User;
import com.dnd.jjigeojulge.user.domain.UserPhotoStyle;
import com.dnd.jjigeojulge.user.domain.UserSetting;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProfileDto(
		@Schema(description = "사용자 ID", example = "1") Long userId,
		@Schema(description = "닉네임", example = "홍길동") String nickname,
		@Schema(description = "성별", example = "MALE") Gender gender,
		@Schema(description = "연령대 (기존 가입자의 경우 null일 수 있음)", nullable = true, example = "TWENTIES") AgeGroup ageGroup,
		@Schema(description = "한줄 소개 (선택 항목)", nullable = true, example = "사진 찍는 걸 좋아합니다.") String introduction,
		@Schema(description = "프로필 이미지 URL", nullable = true, example = "https://example.com/profile.jpg") String profileImageUrl,
		@Schema(description = "선호 촬영 스타일", example = "[\"SNS_UPLOAD\", \"FULL_BODY\"]") List<StyleName> photoStyles,
		@Schema(description = "알림 및 위치 정보 제공 동의 여부") ConsentDto consent) {

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
