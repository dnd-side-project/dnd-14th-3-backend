package com.dnd.jjigeojulge.auth.infra.security;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.OAuthInfo;
import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;
import com.dnd.jjigeojulge.user.domain.User;

class CustomUserDetailsTest {

	@Test
	@DisplayName("User 엔티티로부터 CustomUserDetails를 생성한다.")
	void fromUser() {
		// given
		User user = User.builder()
			.oauthInfo(new OAuthInfo("12345", OAuthProvider.KAKAO))
			.nickname("테스터")
			.gender(Gender.MALE)
			.profileImageUrl("http://image.com")
			.build();

		// when
		CustomUserDetails userDetails = CustomUserDetails.from(user);

		// then
		assertThat(userDetails.getAuthorities()).hasSize(1);
		assertThat(userDetails.getAuthorities().iterator().next().getAuthority())
			.isEqualTo("ROLE_USER");
		assertThat(userDetails.getUsername()).isEqualTo(String.valueOf(user.getId()));
		assertThat(userDetails.getPassword()).isNull();
	}

	@Test
	@DisplayName("UserDetails 기본 계정 상태 메서드들은 항상 true를 반환한다.")
	void accountStatusMethods() {
		// given
		CustomUserDetails userDetails = new CustomUserDetails(1L, List.of());

		// then
		assertThat(userDetails.isAccountNonExpired()).isTrue();
		assertThat(userDetails.isAccountNonLocked()).isTrue();
		assertThat(userDetails.isCredentialsNonExpired()).isTrue();
		assertThat(userDetails.isEnabled()).isTrue();
	}
}
