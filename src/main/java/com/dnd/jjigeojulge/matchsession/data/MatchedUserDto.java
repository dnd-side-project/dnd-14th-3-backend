package com.dnd.jjigeojulge.matchsession.data;

import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.User;

public record MatchedUserDto(
	Long userId,
	String nickname,
	String profileImageUrl,
	Gender gender
) {
	public static MatchedUserDto from(User user) {
		return new MatchedUserDto(
			user.getId(),
			user.getNickname(),
			user.getProfileImageUrl(),
			user.getGender()
		);
	}
}
