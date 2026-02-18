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

	private final SecretKey key;
	private final JwtProperties jwtProperties;

	public enum TokenType {
		ACCESS, REFRESH, REGISTER
	}

	public JwtTokenProvider(JwtProperties jwtProperties) {
		this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
		this.jwtProperties = jwtProperties;
	}

	public String createAccessToken(Long userId) {
		return createToken(String.valueOf(userId), TokenType.ACCESS, jwtProperties.accessTokenExpire());
	}

	public String createRefreshToken(Long userId) {
		return createToken(String.valueOf(userId), TokenType.REFRESH, jwtProperties.refreshTokenExpire());
	}

	public String createRegisterToken(String providerId) {
		return createToken(providerId, TokenType.REGISTER, jwtProperties.registerTokenExpire());
	}

	private String createToken(String subject, TokenType type, long validityInMilliseconds) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder()
			.subject(subject)
			.claim(TYPE_CLAIM, type.name())
			.issuedAt(now)
			.expiration(validity)
			.signWith(key)
			.compact();
	}

	/**
	 * 토큰 유효성 및 타입 검증을 수행합니다.
	 *
	 * @param token 검증할 토큰
	 * @param expectedType 예상되는 토큰 타입
	 * @throws JwtException 만료, 서명 불일치 등 JWT 자체 오류
	 * @throws BusinessException 토큰 타입이 일치하지 않는 경우
	 */
	public void validateToken(String token, TokenType expectedType) {
		Claims claims = parseClaims(token);
		String type = claims.get(TYPE_CLAIM, String.class);
		if (type == null || !type.equals(expectedType.name())) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
	}

	/**
	 * Access Token 전용 검증 메서드
	 */
	public void validateAccessToken(String token) {
		validateToken(token, TokenType.ACCESS);
	}

	/**
	 * Refresh Token 전용 검증 메서드
	 */
	public void validateRefreshToken(String token) {
		validateToken(token, TokenType.REFRESH);
	}

	/**
	 * Register Token 전용 검증 메서드
	 */
	public void validateRegisterToken(String token) {
		validateToken(token, TokenType.REGISTER);
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
