package com.dnd.jjigeojulge.global.common.dto;

import java.util.List;

import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.StyleName;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "작성자 정보 DTO")
public record AuthorDto(
	@Schema(description = "작성자 ID", example = "1")
	Long id,

	@Schema(description = "작성자 닉네임", example = "사진전문가")
	String nickname,

	@Schema(description = "작성자 성별", example = "MALE")
	Gender gender,

	@Schema(description = "작성자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
	String profileImageUrl,

	@Schema(description = "작성자 자기소개", example = "사진 찍는 걸 좋아하는 여행자입니다. 함께 멋진 추억 만들어봐요!")
	String introduction,

	@Schema(description = "작성자 선호 사진 스타일 목록", example = "[\"FULL_BODY\", \"PROP_USAGE\"]")
	List<StyleName> photoStyles
) {
}
