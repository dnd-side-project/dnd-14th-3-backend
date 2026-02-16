package com.dnd.jjigeojulge.user.presentation;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dnd.jjigeojulge.user.application.UserService;
import com.dnd.jjigeojulge.presentation.common.response.ApiResponse;
import com.dnd.jjigeojulge.user.presentation.api.UserApi;
import com.dnd.jjigeojulge.user.presentation.data.ConsentDto;
import com.dnd.jjigeojulge.user.presentation.data.ProfileDto;
import com.dnd.jjigeojulge.user.presentation.request.UserCheckNicknameRequest;
import com.dnd.jjigeojulge.user.presentation.request.UserConsentUpdateRequest;
import com.dnd.jjigeojulge.user.presentation.request.UserUpdateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserApi {

	private final UserService userService;

	@Override
	@PostMapping("/check-nickname")
	public ResponseEntity<ApiResponse<Boolean>> checkNicknameAvailability(
		@RequestBody @Valid UserCheckNicknameRequest request
	) {
		return ResponseEntity.ok(ApiResponse.success(userService.isNicknameAvailable(request)));
	}

	@Override
	@PatchMapping(path = "{userId}/profiles", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<ApiResponse<ProfileDto>> update(@PathVariable Long userId,
		@RequestPart("request") @Valid UserUpdateRequest userUpdateRequest,
		@RequestPart(value = "image", required = false) MultipartFile profileImage) {
		ProfileDto dto = userService.updateProfile(userId, userUpdateRequest, profileImage);
		return ResponseEntity.ok(ApiResponse.success(dto));
	}

	@Override
	@GetMapping("{userId}/profiles")
	public ResponseEntity<ApiResponse<ProfileDto>> find(@PathVariable Long userId) {
		return ResponseEntity.ok(ApiResponse.success(userService.getProfile(userId)));
	}

	@Override
	@GetMapping("{userId}/consents")
	public ResponseEntity<ApiResponse<ConsentDto>> getConsentPermission(@PathVariable Long userId) {
		ConsentDto dto = userService.getConsent(userId);
		return ResponseEntity.ok(ApiResponse.success(dto));
	}

	@Override
	@PatchMapping("{userId}/consents")
	public ResponseEntity<ApiResponse<ConsentDto>> updateConsentPermission(@PathVariable Long userId,
		@RequestBody @Valid UserConsentUpdateRequest request) {
		ConsentDto dto = userService.updateConsent(userId, request);
		return ResponseEntity.ok(ApiResponse.success(dto));
	}
}
