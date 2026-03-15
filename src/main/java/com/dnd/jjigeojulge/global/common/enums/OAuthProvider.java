package com.dnd.jjigeojulge.global.common.enums;

import java.util.Arrays;

public enum OAuthProvider {
	KAKAO("kakao"),
	GOOGLE("google");

	private final String registrationId;

	OAuthProvider(String registrationId) {
		this.registrationId = registrationId;
	}

	// TODO custom exception
	public static OAuthProvider from(String registrationId) {
		return Arrays.stream(values())
			.filter(p -> p.registrationId.equalsIgnoreCase(registrationId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 로그인: " + registrationId));
	}
}
