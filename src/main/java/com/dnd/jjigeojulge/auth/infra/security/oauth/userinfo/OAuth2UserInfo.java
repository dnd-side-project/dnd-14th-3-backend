package com.dnd.jjigeojulge.auth.infra.security.oauth.userinfo;

import java.util.Map;

import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;

import lombok.Getter;

@Getter
public abstract class OAuth2UserInfo {

	protected Map<String, Object> attributes;

	public OAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public abstract OAuthProvider getProvider();

	public abstract String getId();

	public abstract String getName();

	public abstract String getEmail();

	public abstract String getImageUrl();
}
