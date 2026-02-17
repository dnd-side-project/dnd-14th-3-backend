package com.dnd.jjigeojulge.auth.infra.kakao;

import java.time.Duration;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.dnd.jjigeojulge.auth.domain.OAuthClient;
import com.dnd.jjigeojulge.auth.domain.OAuthUserProfile;
import com.dnd.jjigeojulge.auth.domain.exception.InvalidOAuthRequestException;
import com.dnd.jjigeojulge.auth.domain.exception.OAuthServerException;
import com.dnd.jjigeojulge.auth.infra.kakao.dto.KakaoTokenResponse;
import com.dnd.jjigeojulge.auth.infra.kakao.dto.KakaoUserInfoResponse;
import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KakaoClient implements OAuthClient {

	private final KakaoProperties kakaoProperties;
	private final RestClient restClient;

	public KakaoClient(KakaoProperties kakaoProperties) {
		this.kakaoProperties = kakaoProperties;

		ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
			.withConnectTimeout(Duration.ofSeconds(3))
			.withReadTimeout(Duration.ofSeconds(5));

		this.restClient = RestClient.builder()
			.requestFactory(ClientHttpRequestFactoryBuilder.httpComponents().build(settings))
			.defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
				log.error("Kakao Client Error: {} - {}", response.getStatusCode(), new String(response.getBody().readAllBytes()));
				throw new InvalidOAuthRequestException();
			})
			.defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
				log.error("Kakao Server Error: {} - {}", response.getStatusCode(), new String(response.getBody().readAllBytes()));
				throw new OAuthServerException();
			})
			.build();
	}

	@Override
	public OAuthProvider getOAuthProvider() {
		return OAuthProvider.KAKAO;
	}

	@Override
	public String getAccessToken(String authCode) {
		log.info("Requesting Kakao Access Token");

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", kakaoProperties.clientId());
		body.add("redirect_uri", kakaoProperties.redirectUri());
		body.add("code", authCode);

		KakaoTokenResponse response = restClient.post()
			.uri(kakaoProperties.tokenUri())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(body)
			.retrieve()
			.body(KakaoTokenResponse.class);

		if (response == null || response.accessToken() == null) {
			log.error("Kakao Access Token response is null");
			throw new OAuthServerException();
		}

		log.info("Successfully retrieved Kakao Access Token");
		return response.accessToken();
	}

	@Override
	public OAuthUserProfile getUserProfile(String accessToken) {
		log.info("Requesting Kakao User Profile");

		KakaoUserInfoResponse response = restClient.get()
			.uri(kakaoProperties.userInfoUri())
			.header("Authorization", "Bearer " + accessToken)
			.retrieve()
			.body(KakaoUserInfoResponse.class);

		if (response == null) {
			log.error("Kakao User Profile response is null");
			throw new OAuthServerException();
		}

		log.info("Successfully retrieved Kakao User Profile: {}", response.id());
		return response.toOAuthUserProfile();
	}
}
