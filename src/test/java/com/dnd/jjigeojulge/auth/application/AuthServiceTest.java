package com.dnd.jjigeojulge.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.jjigeojulge.auth.application.dto.AuthResult;
import com.dnd.jjigeojulge.auth.application.dto.SignupCommand;
import com.dnd.jjigeojulge.auth.domain.OAuthClient;
import com.dnd.jjigeojulge.auth.domain.OAuthUserProfile;
import com.dnd.jjigeojulge.auth.infra.jwt.JwtTokenProvider;
import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;
import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.OAuthInfo;
import com.dnd.jjigeojulge.user.domain.PhotoStyle;
import com.dnd.jjigeojulge.user.domain.StyleName;
import com.dnd.jjigeojulge.user.domain.User;
import com.dnd.jjigeojulge.user.infra.PhotoStyleRepository;
import com.dnd.jjigeojulge.user.infra.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private OAuthClient oAuthClient;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PhotoStyleRepository photoStyleRepository;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@InjectMocks
	private AuthService authService;

	@Test
	@DisplayName("로그인 - 기존 유저는 액세스/리프레시 토큰을 발급받는다.")
	void loginExistingUser() {
		// given
		String authCode = "kakao_auth_code";
		String accessToken = "kakao_access_token";
		OAuthUserProfile profile = new OAuthUserProfile("12345", OAuthProvider.KAKAO, "http://example.com/profile.jpg");
		User user = User.builder()
				.oauthInfo(new OAuthInfo("12345", OAuthProvider.KAKAO))
				.nickname("test")
				.gender(Gender.MALE)
				.build();
		// 테스트용 ID 설정을 위해 리플렉션이나 모의 객체 설정 필요하지만, 여기서는 any() 매칭 등으로 검증

		given(oAuthClient.getAccessToken(authCode)).willReturn(accessToken);
		given(oAuthClient.getUserProfile(accessToken)).willReturn(profile);
		given(userRepository.findByOAuthInfo(profile.providerId(), profile.provider()))
				.willReturn(Optional.of(user));
		given(jwtTokenProvider.createAccessToken(any())).willReturn("new_access_token");
		given(jwtTokenProvider.createRefreshToken(any())).willReturn("new_refresh_token");

		// when
		AuthResult result = authService.login(authCode);

		// then
		assertThat(result.isNewUser()).isFalse();
		assertThat(result.accessToken()).isEqualTo("new_access_token");
		assertThat(result.refreshToken()).isEqualTo("new_refresh_token");
	}

	@Test
	@DisplayName("로그인 - 신규 유저는 회원가입용 임시 토큰(RegisterToken)을 발급받는다.")
	void loginNewUser() {
		// given
		String authCode = "valid_auth_code";
		String accessToken = "valid_access_token";
		OAuthUserProfile profile = new OAuthUserProfile("new_user_123", OAuthProvider.KAKAO,
				"http://example.com/new_profile.jpg");

		given(oAuthClient.getAccessToken(authCode)).willReturn(accessToken);
		given(oAuthClient.getUserProfile(accessToken)).willReturn(profile);
		given(userRepository.findByOAuthInfo(profile.providerId(), profile.provider()))
				.willReturn(Optional.empty()); // 유저 없음
		given(jwtTokenProvider.createRegisterToken(profile.providerId())).willReturn("register_token");

		// when
		AuthResult result = authService.login(authCode);

		// then
		assertThat(result.isNewUser()).isTrue();
		assertThat(result.registerToken()).isEqualTo("register_token");
		assertThat(result.accessToken()).isNull();
	}

	@Test
	@DisplayName("회원가입 - 정상적인 요청이면 유저를 저장하고 토큰을 발급한다.")
	void signupSuccess() {
		// given
		String registerToken = "valid_register_token";
		SignupCommand command = new SignupCommand(
				registerToken, "nickname", Gender.MALE, com.dnd.jjigeojulge.user.domain.AgeGroup.TWENTIES, "자기소개입니다",
				"url", List.of(StyleName.FULL_BODY));
		String providerId = "kakao_123";

		willDoNothing().given(jwtTokenProvider).validateRegisterToken(registerToken);
		given(jwtTokenProvider.getPayload(registerToken)).willReturn(providerId);
		given(userRepository.existsByNickname(command.nickname())).willReturn(false); // 중복 아님
		given(photoStyleRepository.findAllByNameIn(anyList()))
				.willReturn(Set.of(new PhotoStyle(StyleName.FULL_BODY))); // 스타일 존재
		given(userRepository.save(any(User.class))).willAnswer(invocation -> {
			User u = invocation.getArgument(0);
			ReflectionTestUtils.setField(u, "id", 1L); // ID 설정
			return u;
		});
		given(jwtTokenProvider.createAccessToken(1L)).willReturn("access");
		given(jwtTokenProvider.createRefreshToken(1L)).willReturn("refresh");

		// when
		AuthResult result = authService.signup(command);

		// then
		assertThat(result.isNewUser()).isFalse();
		assertThat(result.accessToken()).isEqualTo("access");
		verify(userRepository).save(any(User.class));
	}

	@Test
	@DisplayName("회원가입 - 닉네임이 중복되면 예외가 발생한다.")
	void signupNicknameDuplicate() {
		// given
		SignupCommand command = new SignupCommand(
				"token", "dup_nick", Gender.MALE, com.dnd.jjigeojulge.user.domain.AgeGroup.TWENTIES, "자기소개입니다", "url",
				List.of());

		willDoNothing().given(jwtTokenProvider).validateRegisterToken(anyString());
		given(jwtTokenProvider.getPayload(anyString())).willReturn("id");
		given(userRepository.existsByNickname("dup_nick")).willReturn(true); // 중복

		// when & then
		assertThatThrownBy(() -> authService.signup(command))
				.isInstanceOf(BusinessException.class)
				.hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONFLICT);
	}

	@Test
	@DisplayName("토큰 재발급 - 리프레시 토큰이 유효하면 액세스 토큰과 리프레시 토큰을 모두 재발급한다.")
	void refreshSuccess() {
		// given
		String refreshToken = "valid_refresh_token";
		String userId = "1";

		willDoNothing().given(jwtTokenProvider).validateRefreshToken(refreshToken);
		given(jwtTokenProvider.getPayload(refreshToken)).willReturn(userId);
		given(jwtTokenProvider.createAccessToken(1L)).willReturn("new_access");
		given(jwtTokenProvider.createRefreshToken(1L)).willReturn("new_refresh");

		// when
		AuthResult result = authService.refresh(refreshToken);

		// then
		assertThat(result.accessToken()).isEqualTo("new_access");
		assertThat(result.refreshToken()).isEqualTo("new_refresh"); // Rotation 확인
	}
}