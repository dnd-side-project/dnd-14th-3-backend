package com.dnd.jjigeojulge.auth.infra.security.oauth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Value("${app.frontendUrl}")
	private String frontendUrl;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException, ServletException {
		// 1. Authentication 객체에서 로그인한 유저 정보를 꺼냅니다.
		// CustomOAuth2UserService에서 반환한 Principal 객체입니다.
		CustomOAuth2User principal = (CustomOAuth2User)authentication.getPrincipal();

		// 2. 유저의 상태(status)를 확인합니다.
		// 만약 CustomOAuth2User라는 별도 클래스를 만드셨다면 더 쉽게 꺼낼 수 있습니다.
		log.info("OAuth2 login success. userId={}, onboardingRequired={}", principal.getUserId(),
			principal.isOnboardingRequired());
		// 3. 상태에 따른 리다이렉트 경로 결정
		if (principal.isOnboardingRequired()) {
			response.sendRedirect(frontendUrl + "onboarding");
			// 온보딩 처리를 할 수 있도록 프론트와 협의 필요
			return;
		}
		// 4. JWT 토큰 발급 및 쿼리 파라미터 추가
		// String token = tokenProvider.createToken(authentication);
		// targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
		//            .queryParam("token", token)
		//            .build().toUriString();

		// 5. 리다이렉트 실행
		response.sendRedirect(frontendUrl);
	}
}
