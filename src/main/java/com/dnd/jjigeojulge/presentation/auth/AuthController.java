package com.dnd.jjigeojulge.presentation.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.jjigeojulge.presentation.auth.api.AuthApi;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

	@Override
	@GetMapping("me")
	public ResponseEntity<String> me(String refreshToken) {
		return ResponseEntity.ok("accessToken");
	}

	@Override
	@PostMapping("refresh")
	public ResponseEntity<String> refresh(String refreshToken, HttpServletResponse response) {
		Cookie refreshTokenCookie = new Cookie("refresh_token", "newRefresh");
		refreshTokenCookie.setHttpOnly(true);
		response.addCookie(refreshTokenCookie);
		return ResponseEntity.ok("newAccessToken");
	}
}
