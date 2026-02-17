package com.dnd.jjigeojulge.auth.presentation.filter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dnd.jjigeojulge.auth.infra.jwt.JwtTokenProvider;
import com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails;

import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

	@Mock
	private FilterChain filterChain;

	@InjectMocks
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@BeforeEach
	void setUp() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("유효한 토큰이 헤더에 있으면 인증 객체가 SecurityContext에 저장된다.")
	void doFilterInternal_ValidToken() throws Exception {
		// given
		String token = "valid_token";
		String userId = "1";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer " + token);
		MockHttpServletResponse response = new MockHttpServletResponse();

		CustomUserDetails userDetails = new CustomUserDetails(1L, List.of(new SimpleGrantedAuthority("ROLE_USER")));

		given(jwtTokenProvider.validateToken(token)).willReturn(true);
		given(jwtTokenProvider.getPayload(token)).willReturn(userId);
		given(userDetailsService.loadUserByUsername(userId)).willReturn(userDetails);

		// when
		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		// then
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
		assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(userDetails);
		then(filterChain).should().doFilter(request, response);
	}

	@Test
	@DisplayName("토큰이 없으면 인증 객체는 null이고 다음 필터로 넘어간다.")
	void doFilterInternal_NoToken() throws Exception {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		// when
		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		// then
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		then(filterChain).should().doFilter(request, response);
	}

	@Test
	@DisplayName("유효하지 않은 토큰이면 인증 객체는 null이고 다음 필터로 넘어간다.")
	void doFilterInternal_InvalidToken() throws Exception {
		// given
		String token = "invalid_token";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer " + token);
		MockHttpServletResponse response = new MockHttpServletResponse();

		given(jwtTokenProvider.validateToken(token)).willReturn(false);

		// when
		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		// then
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		then(filterChain).should().doFilter(request, response);
	}
}
