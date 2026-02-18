package com.dnd.jjigeojulge.auth.domain;

import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;

public interface OAuthClient {
	OAuthProvider getOAuthProvider();

	String getAccessToken(String authCode);

	OAuthUserProfile getUserProfile(String accessToken);
}
