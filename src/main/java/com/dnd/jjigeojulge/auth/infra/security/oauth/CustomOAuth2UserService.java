package com.dnd.jjigeojulge.auth.infra.security.oauth;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.auth.infra.security.oauth.userinfo.OAuth2UserInfo;
import com.dnd.jjigeojulge.auth.infra.security.oauth.userinfo.OAuth2UserInfoFactory;
import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;
import com.dnd.jjigeojulge.user.domain.AgeGroup;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.Introduction;
import com.dnd.jjigeojulge.user.domain.OAuthInfo;
import com.dnd.jjigeojulge.user.domain.User;
import com.dnd.jjigeojulge.user.infra.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		OAuthProvider oAuthProvider = OAuthProvider.from(userRequest.getClientRegistration().getRegistrationId());

		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuthProvider, oAuth2User.getAttributes());

		OAuthProvider provider = userInfo.getProvider();
		String providerId = userInfo.getId();
		User user = userRepository.findByOAuthInfo(providerId, provider)
			.orElseGet(() -> {
				log.info("신규 소셜 사용자. 최소 회원 생성 provider={}, providerId={}", provider, providerId);
				OAuthInfo oAuthInfo = new OAuthInfo(providerId, provider);
				User newUser = User.create(oAuthInfo, userInfo.getName(), Gender.NONE, AgeGroup.NONE,
					Introduction.from(null),
					userInfo.getImageUrl(), Set.of());
				return userRepository.save(newUser);
			});

		return new CustomOAuth2User(user.getId(),
			provider,
			providerId,
			user.getNickname(),
			user.getStatus(),
			List.of(new SimpleGrantedAuthority("ROLE_USER")),
			oAuth2User.getAttributes()
		);
	}
}
