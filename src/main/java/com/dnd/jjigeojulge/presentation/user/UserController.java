package com.dnd.jjigeojulge.presentation.user;

import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dnd.jjigeojulge.global.common.ApiResponse;
import com.dnd.jjigeojulge.presentation.user.api.UserApi;
import com.dnd.jjigeojulge.presentation.user.data.ConsentDto;
import com.dnd.jjigeojulge.presentation.user.data.ProfileDto;
import com.dnd.jjigeojulge.presentation.user.request.UserConsentUpdateRequest;
import com.dnd.jjigeojulge.presentation.user.request.UserUpdateRequest;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController implements UserApi {

	@Override
	@GetMapping("check-nickname")
	public ResponseEntity<ApiResponse<Boolean>> checkNicknameAvailability(@RequestParam String nickname) {
		return ResponseEntity.ok(ApiResponse.success(true));
	}

	@Override
	@PatchMapping(path = "{userId}/profiles", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<ApiResponse<ProfileDto>> update(@PathVariable Long userId,
		@RequestPart("request") @Valid UserUpdateRequest userUpdateRequest,
		@RequestPart(value = "image", required = false) MultipartFile profileImage) {
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@Override
	@GetMapping("{userId}/profiles")
	public ResponseEntity<ApiResponse<ProfileDto>> find(@PathVariable Long userId) {
		ProfileDto dto = new ProfileDto("홍길동", "https://example.com/profile.jpg");
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
