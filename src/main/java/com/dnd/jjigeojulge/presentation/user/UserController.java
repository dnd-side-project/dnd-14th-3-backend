package com.dnd.jjigeojulge.presentation.user;

import java.time.LocalDateTime;

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

import com.dnd.jjigeojulge.application.user.UserService;
import com.dnd.jjigeojulge.global.common.ApiResponse;
import com.dnd.jjigeojulge.presentation.user.api.UserApi;
import com.dnd.jjigeojulge.presentation.user.data.ConsentDto;
import com.dnd.jjigeojulge.presentation.user.data.ProfileDto;
import com.dnd.jjigeojulge.presentation.user.request.UserCheckNicknameRequest;
import com.dnd.jjigeojulge.presentation.user.request.UserConsentUpdateRequest;
import com.dnd.jjigeojulge.presentation.user.request.UserUpdateRequest;

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
		@RequestBody UserCheckNicknameRequest request) {
		return ResponseEntity.ok(ApiResponse.success(userService.isNicknameAvailable(request)));
	}

	@Override
	@PatchMapping(path = "{userId}/profiles", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<ApiResponse<ProfileDto>> update(@PathVariable Long userId,
		@RequestPart("request") @Valid UserUpdateRequest userUpdateRequest,
		@RequestPart(value = "image", required = false) MultipartFile profileImage) {
		String username = "홍길동";
		String profileImageUrl = "https://example.com/profile.jpg";
		String email = "hong@mail.com";
		String phoneNumber = "010-1234-5678";
		ProfileDto dto = new ProfileDto(username, profileImageUrl, email, phoneNumber);
		return ResponseEntity.ok(ApiResponse.success(dto));
	}

	@Override
	@GetMapping("{userId}/profiles")
	public ResponseEntity<ApiResponse<ProfileDto>> find(@PathVariable Long userId) {
		String username = "홍길동";
		String profileImageUrl = "https://example.com/profile.jpg";
		String email = "hong@mail.com";
		String phoneNumber = "010-1234-5678";
		ProfileDto dto = new ProfileDto(username, profileImageUrl, email, phoneNumber);
		return ResponseEntity.ok(ApiResponse.success(dto));
	}

	@Override
	@GetMapping("{userId}/consents")
	public ResponseEntity<ApiResponse<ConsentDto>> getConsentPermission(@PathVariable Long userId) {
		ConsentDto dto = new ConsentDto(false, false, LocalDateTime.now());
		return ResponseEntity.ok(ApiResponse.success(dto));
	}

	@Override
	@PatchMapping("{userId}/consents")
	public ResponseEntity<ApiResponse<ConsentDto>> updateConsentPermission(@PathVariable Long userId,
		@RequestBody @Valid UserConsentUpdateRequest request) {
		ConsentDto dto = new ConsentDto(false, false, LocalDateTime.now());
		return ResponseEntity.ok(ApiResponse.success(dto));
	}
}
