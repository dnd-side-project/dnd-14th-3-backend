package com.dnd.jjigeojulge.auth.infra.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private final SecretKey key;
	private final JwtProperties jwtProperties;

	public JwtTokenProvider(JwtProperties jwtProperties) {
		this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
		this.jwtProperties = jwtProperties;
	}

	public String createAccessToken(Long userId) {
		return createToken(String.valueOf(userId), jwtProperties.accessTokenExpire());
	}

	public String createRefreshToken(Long userId) {
		return createToken(String.valueOf(userId), jwtProperties.refreshTokenExpire());
	}

	public String createRegisterToken(String providerId) {
		return createToken(providerId, jwtProperties.registerTokenExpire());
	}

	private String createToken(String subject, long validityInMilliseconds) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder()
			.subject(subject)
			.issuedAt(now)
			.expiration(validity)
			.signWith(key)
			.compact();
	}

	public void validateToken(String token) {
		Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token);
	}

	public String getPayload(String token) {
		return Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getSubject();
	}
}
