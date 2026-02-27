package com.dnd.jjigeojulge.auth.domain;

import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;

public record OAuthUserProfile(
		String providerId,
		OAuthProvider provider,
		String profileImageUrl) {
}
