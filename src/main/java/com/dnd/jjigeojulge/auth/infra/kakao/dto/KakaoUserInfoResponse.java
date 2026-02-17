package com.dnd.jjigeojulge.auth.infra.kakao.dto;

import com.dnd.jjigeojulge.auth.domain.OAuthUserProfile;
import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
	Long id
) {
	public OAuthUserProfile toOAuthUserProfile() {
		return new OAuthUserProfile(
			String.valueOf(id),
			OAuthProvider.KAKAO
		);
	}
}
