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
import org.springframework.web.bind.annotation.CookieValue;
import com.dnd.jjigeojulge.auth.presentation.response.LoginResponse;
import com.dnd.jjigeojulge.auth.presentation.response.TokenResponse;
import com.dnd.jjigeojulge.global.common.response.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.dnd.jjigeojulge.auth.infra.jwt.JwtProperties;
import com.dnd.jjigeojulge.global.util.CookieUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApi {

	private final AuthService authService;
	private final JwtProperties jwtProperties;

	@Override
	public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestParam String code, HttpServletResponse response) {
		AuthResult authResult = authService.login(code);

		LoginResponse loginResponse;
		String message;

		if (authResult instanceof AuthResult.RegisterNeeded needed) {
			CookieUtils.deleteCookie(response, "refresh_token");
			loginResponse = LoginResponse.registerNeeded(needed.registerToken(), needed.profileImageUrl());
			message = "회원가입이 필요합니다.";
		} else if (authResult instanceof AuthResult.Success success) {
			setRefreshTokenCookie(response, success.refreshToken());
			loginResponse = LoginResponse.loginSuccess(
					TokenResponse.of(success.accessToken()));
			message = "로그인 성공";
		} else {
			throw new IllegalStateException("Unexpected AuthResult type");
		}

		return ResponseEntity.ok(ApiResponse.success(message, loginResponse));
	}

	@Override
	public ResponseEntity<ApiResponse<TokenResponse>> signup(
			@RequestHeader("Register-Token") String registerToken,
			@Valid @RequestBody SignupRequest request,
			HttpServletResponse response) {
		SignupCommand command = request.toCommand(registerToken);
		AuthResult result = authService.signup(command);

		if (result instanceof AuthResult.Success success) {
			setRefreshTokenCookie(response, success.refreshToken());
			return ResponseEntity.ok(ApiResponse.success(
					"회원가입 성공",
					TokenResponse.of(success.accessToken())));
		}
		throw new IllegalStateException("Signup must return Success");
	}

	@Override
	public ResponseEntity<ApiResponse<TokenResponse>> refresh(
			@CookieValue(name = "refresh_token") String refreshToken,
			HttpServletResponse response) {
		AuthResult result = authService.refresh(refreshToken);

		if (result instanceof AuthResult.Success success) {
			setRefreshTokenCookie(response, success.refreshToken());
			return ResponseEntity.ok(ApiResponse.success(
					"토큰 재발급 성공",
					TokenResponse.of(success.accessToken())));
		}
		throw new IllegalStateException("Refresh must return Success");
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> verifySession() {
		// JwtAuthenticationFilter를 무사히 통과했다면 유효한 세션이므로 200 OK를 반환
		return ResponseEntity.ok(ApiResponse.success("유효한 세션입니다.", null));
	}

	private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		CookieUtils.addCookie(response, "refresh_token", refreshToken,
				(int) (jwtProperties.refreshTokenExpire() / 1000));
	}
}
