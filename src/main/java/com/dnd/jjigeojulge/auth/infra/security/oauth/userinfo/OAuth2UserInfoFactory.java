package com.dnd.jjigeojulge.auth.infra.security.oauth.userinfo;

import java.util.Map;

import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;

public class OAuth2UserInfoFactory {

	private OAuth2UserInfoFactory() {
	}

	public static OAuth2UserInfo getOAuth2UserInfo(OAuthProvider provider, Map<String, Object> attributes) {
		return switch (provider) {
			case KAKAO -> new KakaoOAuth2UserInfo(attributes);
			case GOOGLE -> throw new RuntimeException("");
			default -> throw new IllegalArgumentException("지원하지 않는 OAuth provider 입니다: " + provider);
		};
	}
}
