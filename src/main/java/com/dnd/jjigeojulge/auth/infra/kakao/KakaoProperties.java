package com.dnd.jjigeojulge.auth.infra.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoProperties(
	String clientId,
	String redirectUri,
	String tokenUri,
	String userInfoUri
) {
}
