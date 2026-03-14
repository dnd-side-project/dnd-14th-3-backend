package com.dnd.jjigeojulge.auth.infra.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class SecurityMatchers {
	public static final RequestMatcher LOGIN_KAKAO = new AntPathRequestMatcher(
		"/api/v1/auth/login/kakao", HttpMethod.GET.name());

	public static final RequestMatcher SIGNUP = new AntPathRequestMatcher(
		"/api/v1/auth/signup", HttpMethod.POST.name());

	public static final RequestMatcher TOKEN_REFRESH = new AntPathRequestMatcher(
		"/api/v1/auth/refresh", HttpMethod.POST.name());

	public static final RequestMatcher LOGOUT = new AntPathRequestMatcher(
		"/api/v1/auth/logout", HttpMethod.POST.name());

	public static final RequestMatcher CHECK_NICKNAME = new AntPathRequestMatcher(
		"/api/v1/users/check-nickname", HttpMethod.GET.name());

	public static final RequestMatcher PHOTO_STYLE = new AntPathRequestMatcher(
		"/api/v1/photo-style", HttpMethod.GET.name());

	public static final RequestMatcher HEALTH_CHECK = new AntPathRequestMatcher(
		"/health", HttpMethod.GET.name());

	public static final RequestMatcher SWAGGER_UI = new AntPathRequestMatcher(
		"/swagger-ui/**");

	public static final RequestMatcher OPENAPI_DOCS = new AntPathRequestMatcher(
		"/v3/api-docs/**");

	public static final RequestMatcher OPENAPI_YAML = new AntPathRequestMatcher(
		"/v3/api-docs.yaml");

	public static final RequestMatcher API_EXAMPLES = new AntPathRequestMatcher(
		"/api/v1/examples/**");

	public static final RequestMatcher WEBSOCKET = new AntPathRequestMatcher(
		"/ws/**");

	public static final RequestMatcher[] PUBLIC_MATCHERS = new RequestMatcher[] {
		LOGIN_KAKAO,
		SIGNUP,
		TOKEN_REFRESH,
		LOGOUT,
		CHECK_NICKNAME,
		PHOTO_STYLE,
		HEALTH_CHECK,
		SWAGGER_UI,
		OPENAPI_DOCS,
		OPENAPI_YAML,
		API_EXAMPLES,
		WEBSOCKET
	};
}
