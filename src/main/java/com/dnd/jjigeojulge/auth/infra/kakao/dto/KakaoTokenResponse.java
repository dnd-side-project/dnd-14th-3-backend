package com.dnd.jjigeojulge.auth.infra.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoTokenResponse(
	String accessToken,
	String refreshToken,
	String tokenType,
	Integer expiresIn,
	Integer refreshTokenExpiresIn,
	String scope
) {
}
