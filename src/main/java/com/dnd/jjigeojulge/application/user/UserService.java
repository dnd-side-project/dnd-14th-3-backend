package com.dnd.jjigeojulge.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dnd.jjigeojulge.domain.user.User;
import com.dnd.jjigeojulge.domain.user.UserSetting;
import com.dnd.jjigeojulge.global.exception.user.UserNotFoundException;
import com.dnd.jjigeojulge.infra.user.UserRepository;
import com.dnd.jjigeojulge.presentation.user.data.ConsentDto;
import com.dnd.jjigeojulge.presentation.user.data.ProfileDto;
import com.dnd.jjigeojulge.presentation.user.request.UserCheckNicknameRequest;
import com.dnd.jjigeojulge.presentation.user.request.UserConsentUpdateRequest;
import com.dnd.jjigeojulge.presentation.user.request.UserUpdateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public boolean isNicknameAvailable(UserCheckNicknameRequest request) {
		return !userRepository.existsByNickname(request.nickname());
	}

	@Transactional(readOnly = true)
	public ProfileDto getProfile(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
		return new ProfileDto(user.getNickname(), user.getProfileImageUrl(), user.getKakaoUserEmail(),
			user.getPhoneNumber());
	}

	@Transactional
	public ProfileDto updateProfile(
		Long userId,
		UserUpdateRequest request,
		MultipartFile profileImage
	) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
		user.update(request.newUsername(), null, null, null);
		return new ProfileDto(user.getNickname(), user.getProfileImageUrl(), user.getKakaoUserEmail(),
			user.getPhoneNumber());
	}

	@Transactional
	public ConsentDto getConsent(Long userId) {
		User user = userRepository.findByUserIdWithUserSetting(userId)
			.orElseThrow(UserNotFoundException::new);
		UserSetting userSetting = getOrCreateUserSetting(user);
		return toDto(userSetting);
	}

	@Transactional
	public ConsentDto updateConsent(Long userId, UserConsentUpdateRequest request) {
		User user = userRepository.findByUserIdWithUserSetting(userId)
			.orElseThrow(UserNotFoundException::new);
		UserSetting userSetting = getOrCreateUserSetting(user);
		userSetting.updateSettings(request.notificationAllowed(), request.locationAllowed());
		return toDto(userSetting);
	}

	private UserSetting getOrCreateUserSetting(User user) {
		UserSetting userSetting = user.getUserSetting();
		if (userSetting != null) {
			return userSetting;
		}
		UserSetting defaultSetting = UserSetting.builder()
			.notificationEnabled(false)
			.locationSharingEnabled(false)
			.build();
		user.setUserSetting(defaultSetting);
		log.warn("UserSetting missing. Creating default setting. userId={}", user.getId());
		return defaultSetting;
	}

	private ConsentDto toDto(UserSetting userSetting) {
		return new ConsentDto(
			userSetting.isNotificationEnabled(),
			userSetting.isLocationSharingEnabled(),
			userSetting.getUpdatedAt()
		);
	}
}
