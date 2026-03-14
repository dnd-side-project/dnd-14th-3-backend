package com.dnd.jjigeojulge.auth.infra.security.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Value("${app.frontend-url}")
	private String frontendUrl;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {
		
		log.error("OAuth2 login failed", exception);

		String errorMessage = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
		response.sendRedirect(frontendUrl + "/login/fail?message=" + errorMessage);
	}
}
