package com.dnd.jjigeojulge.application.user;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dnd.jjigeojulge.presentation.user.data.ConsentDto;
import com.dnd.jjigeojulge.presentation.user.data.ProfileDto;
import com.dnd.jjigeojulge.presentation.user.request.UserCheckNicknameRequest;
import com.dnd.jjigeojulge.presentation.user.request.UserUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	public boolean isNicknameAvailable(UserCheckNicknameRequest request) {
		// TODO
		return false;
	}

	public ProfileDto getProfile(Long userId) {
		// TODO
		return null;
	}

	public ProfileDto updateProfile(
		Long userId,
		UserUpdateRequest request,
		MultipartFile profileImage
	) {
		// TODO
		return null;
	}

	public ConsentDto getConsent(Long userId) {
		// TODO
		return null;
	}

	public ConsentDto updateConsent(Long userId, UserUpdateRequest request) {
		// TODO
		return null;
	}

}
