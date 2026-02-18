package com.dnd.jjigeojulge.auth.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.jjigeojulge.auth.application.AuthService;
import com.dnd.jjigeojulge.auth.application.dto.AuthResult;
import com.dnd.jjigeojulge.auth.application.dto.SignupCommand;
import com.dnd.jjigeojulge.auth.presentation.api.AuthApi;
import com.dnd.jjigeojulge.auth.presentation.request.SignupRequest;
import com.dnd.jjigeojulge.auth.presentation.response.LoginResponse;
import com.dnd.jjigeojulge.auth.presentation.response.TokenResponse;
import com.dnd.jjigeojulge.global.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApi {

	private final AuthService authService;

	@Override
	public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestParam String code) {
		AuthResult result = authService.login(code);

		LoginResponse response;
		if (result.isNewUser()) {
			response = LoginResponse.registerNeeded(result.registerToken());
		} else {
			response = LoginResponse.loginSuccess(
				TokenResponse.of(result.accessToken(), result.refreshToken())
			);
		}

		return ResponseEntity.ok(ApiResponse.success(
			result.isNewUser() ? "회원가입이 필요합니다." : "로그인 성공",
			response
		));
	}

	@Override
	public ResponseEntity<ApiResponse<TokenResponse>> signup(
		@RequestHeader("Register-Token") String registerToken,
		@Valid @RequestBody SignupRequest request
	) {
		SignupCommand command = request.toCommand(registerToken);
		AuthResult result = authService.signup(command);

		return ResponseEntity.ok(ApiResponse.success(
			"회원가입 성공",
			TokenResponse.of(result.accessToken(), result.refreshToken())
		));
	}

	@Override
	public ResponseEntity<ApiResponse<TokenResponse>> refresh(
		@RequestHeader("Authorization") String refreshToken
	) {
		String token = refreshToken;
		if (refreshToken.startsWith("Bearer ")) {
			token = refreshToken.substring(7);
		}
		AuthResult result = authService.refresh(token);
		return ResponseEntity.ok(ApiResponse.success(
			"토큰 재발급 성공",
			TokenResponse.of(result.accessToken(), result.refreshToken())
		));
	}
}
