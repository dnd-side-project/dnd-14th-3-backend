package com.dnd.jjigeojulge.auth.infra.security;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

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
			.oauthInfo(new OAuthInfo("test-id", OAuthProvider.KAKAO))
			.nickname("test")
			.gender(Gender.MALE)
			.build();
		ReflectionTestUtils.setField(user, "id", 1L);

		// when
		CustomUserDetails userDetails = CustomUserDetails.from(user);

		// then
		assertThat(userDetails.id()).isEqualTo(1L);
		assertThat(userDetails.getUsername()).isEqualTo("1");
		assertThat(userDetails.getAuthorities()).hasSize(1);
		assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
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