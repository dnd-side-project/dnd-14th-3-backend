package com.dnd.jjigeojulge.auth.infra.security.oauth.userinfo;

import java.util.Map;
import java.util.Objects;

import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {
	public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public OAuthProvider getProvider() {
		return OAuthProvider.KAKAO;
	}

	@Override
	public String getId() {
		Object id = attributes.get("id");
		if (Objects.isNull(id)) {
			throw new IllegalArgumentException("카카오 provider id가 없습니다.");
		}
		return String.valueOf(id);
	}

	@Override
	public String getName() {
		return getAttribute("nickname");
	}

	@Override
	public String getEmail() {
		return getAttribute("email");
	}

	@Override
	public String getImageUrl() {
		return getAttribute("profile_image");
	}

	@SuppressWarnings("unchecked")
	private String getAttribute(String fieldName) {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("properties");
		if (kakaoAccount == null) {
			return null;
		}

		Object value = kakaoAccount.get(fieldName);
		return value != null ? String.valueOf(value) : null;
	}

}
