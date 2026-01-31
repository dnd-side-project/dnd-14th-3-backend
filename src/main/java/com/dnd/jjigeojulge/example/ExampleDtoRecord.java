package com.dnd.jjigeojulge.example;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record ExampleDtoRecord(
	@Schema(description = "사용자 ID", example = "1")
	Long id,
	@Schema(description = "사용자 이메일", example = "example@google.com")
	String email,
	@Schema(description = "사용자 프로필 정보")
	Profile profile,
	List<PostSummary> posts
) {

	public record Profile(
		@Schema(description = "닉네임", example = "exampleNickname")
		String nickname,
		@Schema(description = "프로필 이미지 URL", example = "http://example.com/profile.jpg")
		String profileImageUrl
	) {
	}

	public record PostSummary(
		@Schema(description = "게시글 ID", example = "10")
		Long postId,
		@Schema(description = "게시글 제목", example = "Example Post Title")
		String title,
		@Schema(description = "게시글 생성 일시", example = "2024-01-01T12:00:00")
		LocalDateTime createdAt
	) {
	}

}
