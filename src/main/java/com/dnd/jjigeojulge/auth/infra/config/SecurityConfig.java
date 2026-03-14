package com.dnd.jjigeojulge.auth.infra.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.dnd.jjigeojulge.auth.infra.security.CustomAuthenticationEntryPoint;
import com.dnd.jjigeojulge.auth.infra.security.JwtLogoutHandler;
import com.dnd.jjigeojulge.auth.infra.security.SecurityMatchers;
import com.dnd.jjigeojulge.auth.infra.security.oauth.CustomOAuth2UserService;
import com.dnd.jjigeojulge.auth.infra.security.oauth.OAuth2AuthenticationFailureHandler;
import com.dnd.jjigeojulge.auth.infra.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.dnd.jjigeojulge.auth.presentation.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2AuthenticationSuccessHandler customOAuth2SuccessHandler;
	private final OAuth2AuthenticationFailureHandler customOAuth2FailureHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(customAuthenticationEntryPoint))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(SecurityMatchers.PUBLIC_MATCHERS)
				.permitAll()
				.anyRequest().authenticated())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		http
			.oauth2Login((oauth2) -> oauth2
				.userInfoEndpoint((userInfo) -> userInfo
					.userService(customOAuth2UserService)
				)
				.successHandler(customOAuth2SuccessHandler)
				.failureHandler(customOAuth2FailureHandler)
			);

		http
			.logout(logout -> logout
				.logoutRequestMatcher(SecurityMatchers.LOGOUT)
				.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
				.addLogoutHandler(new JwtLogoutHandler())
			);
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of("*")); // 개발 편의를 위해 모든 도메인 허용
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3000L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		return RoleHierarchyImpl.withDefaultRolePrefix()
			.build();
	}

}
