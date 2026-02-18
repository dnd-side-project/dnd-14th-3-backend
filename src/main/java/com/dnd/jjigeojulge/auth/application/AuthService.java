package com.dnd.jjigeojulge.auth.application;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.auth.application.dto.AuthResult;
import com.dnd.jjigeojulge.auth.application.dto.SignupCommand;
import com.dnd.jjigeojulge.auth.domain.OAuthClient;
import com.dnd.jjigeojulge.auth.domain.OAuthUserProfile;
import com.dnd.jjigeojulge.auth.infra.jwt.JwtTokenProvider;
import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;
import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;
import com.dnd.jjigeojulge.user.domain.OAuthInfo;
import com.dnd.jjigeojulge.user.domain.PhotoStyle;
import com.dnd.jjigeojulge.user.domain.User;
import com.dnd.jjigeojulge.user.domain.exception.InvalidPhotoStyleException;
import com.dnd.jjigeojulge.user.infra.PhotoStyleRepository;
import com.dnd.jjigeojulge.user.infra.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final OAuthClient oAuthClient;
	private final UserRepository userRepository;
	private final PhotoStyleRepository photoStyleRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public AuthResult login(String authCode) {
		String accessToken = oAuthClient.getAccessToken(authCode);
		OAuthUserProfile userProfile = oAuthClient.getUserProfile(accessToken);

		return userRepository.findByOAuthInfo(userProfile.providerId(), userProfile.provider())
			.map(user -> {
				String access = jwtTokenProvider.createAccessToken(user.getId());
				String refresh = jwtTokenProvider.createRefreshToken(user.getId());
				return AuthResult.success(access, refresh);
			})
			.orElseGet(() -> {
				String registerToken = jwtTokenProvider.createRegisterToken(userProfile.providerId());
				return AuthResult.registerNeeded(registerToken);
			});
	}

	public AuthResult signup(SignupCommand command) {
		jwtTokenProvider.validateRegisterToken(command.registerToken());
		String providerId = jwtTokenProvider.getPayload(command.registerToken());

		if (userRepository.existsByNickname(command.nickname())) {
			throw new BusinessException(ErrorCode.CONFLICT);
		}

		Set<PhotoStyle> photoStyles = photoStyleRepository.findAllByNameIn(command.photoStyles());
		if (photoStyles.size() != command.photoStyles().size()) {
			throw new InvalidPhotoStyleException(ErrorCode.INVALID_PHOTO_STYLE);
		}

		User user = User.create(
			new OAuthInfo(providerId, OAuthProvider.KAKAO),
			command.nickname(),
			command.gender(),
			command.profileImageUrl(),
			photoStyles
		);

		// TODO: [Refactor] AuthService에서 유저 관리를 직접 하지 않고, UserService나 Port로 위임 필요
		User savedUser = userRepository.save(user);

		String access = jwtTokenProvider.createAccessToken(savedUser.getId());
		String refresh = jwtTokenProvider.createRefreshToken(savedUser.getId());

		return AuthResult.success(access, refresh);
	}

	public AuthResult refresh(String refreshToken) {
		jwtTokenProvider.validateRefreshToken(refreshToken);
		String userId = jwtTokenProvider.getPayload(refreshToken);

		long id = Long.parseLong(userId);
		String newAccessToken = jwtTokenProvider.createAccessToken(id);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(id);

		return AuthResult.success(newAccessToken, newRefreshToken);
	}
}
