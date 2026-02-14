package com.dnd.jjigeojulge.application.user;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dnd.jjigeojulge.domain.photostyle.PhotoStyle;
import com.dnd.jjigeojulge.domain.user.User;
import com.dnd.jjigeojulge.domain.user.UserSetting;
import com.dnd.jjigeojulge.global.exception.user.UserNotFoundException;
import com.dnd.jjigeojulge.infra.user.PhotoStyleRepository;
import com.dnd.jjigeojulge.infra.user.UserRepository;
import com.dnd.jjigeojulge.presentation.user.data.ConsentDto;
import com.dnd.jjigeojulge.presentation.user.data.ProfileDto;
import com.dnd.jjigeojulge.presentation.user.request.UserCheckNicknameRequest;
import com.dnd.jjigeojulge.presentation.user.request.UserConsentUpdateRequest;
import com.dnd.jjigeojulge.presentation.user.request.UserUpdateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO 프로필 관련 조회 설정 PreAuthorize 적용 필요, 스프링 시큐리티
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PhotoStyleRepository photoStyleRepository;

	@Transactional(readOnly = true)
	public boolean isNicknameAvailable(UserCheckNicknameRequest request) {
		return !userRepository.existsByNickname(request.nickname());
	}

	@Transactional(readOnly = true)
	public ProfileDto getProfile(Long userId) {
		User user = userRepository.findByIdWithPhotoStyles(userId)
			.orElseThrow(UserNotFoundException::new);
		return toDto(user);
	}

	@Transactional
	public ProfileDto updateProfile(
		Long userId,
		UserUpdateRequest request,
		MultipartFile profileImage    //TODO  프로필 이미지 업데이트 향후 구현
	) {
		User user = userRepository.findByIdWithPhotoStyles(userId)
			.orElseThrow(UserNotFoundException::new);

		Set<PhotoStyle> photoStyles = photoStyleRepository.findAllByNameIn(request.preferredStyles());
		user.update(request.newUsername(), request.gender(), photoStyles);
		return toDto(user);
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
		return ConsentDto.from(userSetting);
	}

	private ProfileDto toDto(User user) {
		return ProfileDto.from(user);
	}
}
