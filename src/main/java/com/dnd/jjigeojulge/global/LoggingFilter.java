package com.dnd.jjigeojulge.global;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		String requestId = determineRequestId(request);
		String requestURI = request.getRequestURI();
		long start = System.currentTimeMillis();

		MDC.put("requestId", requestId);
		MDC.put("requestURI", requestURI);
		response.setHeader("X-Request-ID", requestId);

		try {
			filterChain.doFilter(request, response);
		} finally {
			long durationMs = System.currentTimeMillis() - start;

			if (response.getStatus() < 400) {
				log.info("API_REQUEST method={} uri={} status={} duration={}ms",
					request.getMethod(),
					request.getRequestURI(),
					response.getStatus(),
					durationMs
				);
			}

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

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String requestURI = request.getRequestURI();
		return requestURI.startsWith("/actuator")
			|| requestURI.startsWith("/swagger")
			|| requestURI.startsWith("/v3/api-docs");
	}
}
