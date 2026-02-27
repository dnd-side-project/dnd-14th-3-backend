package com.dnd.jjigeojulge.auth.infra.kakao.dto;

import com.dnd.jjigeojulge.auth.domain.OAuthUserProfile;
import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
		Long id,
		KakaoAccount kakaoAccount) {

	public OAuthUserProfile toOAuthUserProfile() {
		String profileImageUrl = null;
		if (kakaoAccount != null && kakaoAccount.profile() != null) {
			profileImageUrl = kakaoAccount.profile().profileImageUrl();
		}

		return new OAuthUserProfile(
				String.valueOf(id),
				OAuthProvider.KAKAO,
				profileImageUrl);
	}

	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public record KakaoAccount(Profile profile) {
	}

	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public record Profile(String profileImageUrl) {
	}
}
