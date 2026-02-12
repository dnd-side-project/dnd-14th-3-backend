package com.dnd.jjigeojulge.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final List<String> EXCLUDE_PATHS = List.of(
		"/swagger-ui/**",
		"/v3/api-docs/**",
		"/health",
		"/favicon.ico"
	);

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
			.allowedHeaders("*")
			.maxAge(3000);
	}

	@Bean
	public MDCLoggingInterceptor mdcLoggingInterceptor() {
		return new MDCLoggingInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(mdcLoggingInterceptor())
			.addPathPatterns("/**")
			.excludePathPatterns(EXCLUDE_PATHS); // 모든 경로에 적용
	}
}
