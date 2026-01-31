package com.dnd.jjigeojulge.common;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		String requestId = determineRequestId(request);

		MDC.put("requestId", requestId);
		response.setHeader("X-Request-ID", requestId);

		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	private String determineRequestId(HttpServletRequest request) {
		String requestId = request.getHeader("X-Request-ID");
		if (requestId == null || requestId.isBlank()) {
			requestId = UUID.randomUUID().toString();
		}
		return requestId;
	}
}
