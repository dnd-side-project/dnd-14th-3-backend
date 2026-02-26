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
		AuthResult result = authService.login(code);

		LoginResponse loginResponse;
		if (result.isNewUser()) {
			loginResponse = LoginResponse.registerNeeded(result.registerToken());
		} else {
			CookieUtils.addCookie(response, "refresh_token", result.refreshToken(),
					(int) (jwtProperties.refreshTokenExpire() / 1000));
			loginResponse = LoginResponse.loginSuccess(
					TokenResponse.of(result.accessToken()));
		}

		return ResponseEntity.ok(ApiResponse.success(
				result.isNewUser() ? "회원가입이 필요합니다." : "로그인 성공",
				loginResponse));
	}

	@Override
	public ResponseEntity<ApiResponse<TokenResponse>> signup(
			@RequestHeader("Register-Token") String registerToken,
			@Valid @RequestBody SignupRequest request,
			HttpServletResponse response) {
		SignupCommand command = request.toCommand(registerToken);
		AuthResult result = authService.signup(command);

		CookieUtils.addCookie(response, "refresh_token", result.refreshToken(),
				(int) (jwtProperties.refreshTokenExpire() / 1000));

		return ResponseEntity.ok(ApiResponse.success(
				"회원가입 성공",
				TokenResponse.of(result.accessToken())));
	}

	@Override
	public ResponseEntity<ApiResponse<TokenResponse>> refresh(
			@CookieValue(name = "refresh_token") String refreshToken,
			HttpServletResponse response) {
		AuthResult result = authService.refresh(refreshToken);

		CookieUtils.addCookie(response, "refresh_token", result.refreshToken(),
				(int) (jwtProperties.refreshTokenExpire() / 1000));

		return ResponseEntity.ok(ApiResponse.success(
				"토큰 재발급 성공",
				TokenResponse.of(result.accessToken())));
	}

	@Override
	public ResponseEntity<ApiResponse<Void>> verifySession() {
		// JwtAuthenticationFilter를 무사히 통과했다면 유효한 세션이므로 200 OK를 반환
		return ResponseEntity.ok(ApiResponse.success("유효한 세션입니다.", null));
	}
}
