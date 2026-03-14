package com.dnd.jjigeojulge.auth.infra.security.oauth;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;
import com.dnd.jjigeojulge.user.domain.UserStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {
	private final Long userId;
	private final OAuthProvider provider;
	private final String providerId;
	private final String nickname;
	private final UserStatus status;
	private final Collection<? extends GrantedAuthority> authorities;
	private final Map<String, Object> attributes;

	@Override
	public String getName() {
		return nickname;
	}

	public boolean isOnboardingRequired() {
		return status == UserStatus.PENDING;
	}
}
