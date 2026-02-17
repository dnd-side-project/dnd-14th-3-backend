package com.dnd.jjigeojulge.auth.infra.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
	String secret,
	Long accessTokenExpire,
	Long refreshTokenExpire,
	Long registerTokenExpire
) {
}