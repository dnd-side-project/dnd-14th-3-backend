package com.dnd.jjigeojulge.auth.infra.jwt;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

	private JwtTokenProvider jwtTokenProvider;
	private JwtProperties jwtProperties;

	@BeforeEach
	void setUp() {
		jwtProperties = new JwtProperties(
			"test_secret_key_for_jjigeojulge_project_1234567890",
			3600000L, // 1시간
			1209600000L, // 2주
			1800000L // 30분
		);
		jwtTokenProvider = new JwtTokenProvider(jwtProperties);
	}

	@Test
	@DisplayName("액세스 토큰을 생성하고 페이로드를 추출한다.")
	void createAccessTokenAndGetPayload() {
		// given
		Long userId = 1L;

		// when
		String token = jwtTokenProvider.createAccessToken(userId);
		String payload = jwtTokenProvider.getPayload(token);

		// then
		assertThat(token).isNotNull();
		assertThat(payload).isEqualTo(String.valueOf(userId));
	}

	@Test
	@DisplayName("회원가입용 임시 토큰을 생성하고 페이로드를 추출한다.")
	void createRegisterTokenAndGetPayload() {
		// given
		String providerId = "kakao_12345";

		// when
		String token = jwtTokenProvider.createRegisterToken(providerId);
		String payload = jwtTokenProvider.getPayload(token);

		// then
		assertThat(token).isNotNull();
		assertThat(payload).isEqualTo(providerId);
	}

	@Test
	@DisplayName("유효한 토큰을 검증하면 예외가 발생하지 않는다.")
	void validateTokenSuccess() {
		// given
		String token = jwtTokenProvider.createAccessToken(1L);

		// when & then
		assertThatCode(() -> jwtTokenProvider.validateToken(token))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("만료된 토큰을 검증하면 예외가 발생한다.")
	void validateTokenExpired() {
		// given
		// 만료 시간이 0인 토큰 생성기 임시 생성
		JwtProperties expiredProperties = new JwtProperties(
			jwtProperties.secret(),
			0L, 0L, 0L
		);
		JwtTokenProvider expiredProvider = new JwtTokenProvider(expiredProperties);
		String token = expiredProvider.createAccessToken(1L);

		// when & then
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(token))
			.isInstanceOf(io.jsonwebtoken.JwtException.class);
	}

	@Test
	@DisplayName("잘못된 서명의 토큰을 검증하면 예외가 발생한다.")
	void validateTokenInvalidSignature() {
		// given
		String token = jwtTokenProvider.createAccessToken(1L);
		String invalidToken = token + "wrong";

		// when & then
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(invalidToken))
			.isInstanceOf(io.jsonwebtoken.JwtException.class);
	}
}
