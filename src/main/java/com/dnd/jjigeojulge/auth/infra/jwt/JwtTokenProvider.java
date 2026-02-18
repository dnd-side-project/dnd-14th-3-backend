package com.dnd.jjigeojulge.auth.infra.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private static final String TYPE_CLAIM = "type";
	private static final String ACCESS = "access";
	private static final String REFRESH = "refresh";
	private static final String REGISTER = "register";

	private final SecretKey key;
	private final JwtProperties jwtProperties;

	public JwtTokenProvider(JwtProperties jwtProperties) {
		this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
		this.jwtProperties = jwtProperties;
	}

	public String createAccessToken(Long userId) {
		return createToken(String.valueOf(userId), ACCESS, jwtProperties.accessTokenExpire());
	}

	public String createRefreshToken(Long userId) {
		return createToken(String.valueOf(userId), REFRESH, jwtProperties.refreshTokenExpire());
	}

	public String createRegisterToken(String providerId) {
		return createToken(providerId, REGISTER, jwtProperties.registerTokenExpire());
	}

	private String createToken(String subject, String type, long validityInMilliseconds) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder()
			.subject(subject)
			.claim(TYPE_CLAIM, type)
			.issuedAt(now)
			.expiration(validity)
			.signWith(key)
			.compact();
	}

	public void validateToken(String token) {
		parseClaims(token);
	}

	public void validateRefreshToken(String token) {
		validateTokenWithType(token, REFRESH);
	}

	public void validateRegisterToken(String token) {
		validateTokenWithType(token, REGISTER);
	}

	private void validateTokenWithType(String token, String expectedType) {
		Claims claims = parseClaims(token);
		String type = claims.get(TYPE_CLAIM, String.class);
		if (type == null || !type.equals(expectedType)) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
	}

	public String getPayload(String token) {
		return parseClaims(token).getSubject();
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}