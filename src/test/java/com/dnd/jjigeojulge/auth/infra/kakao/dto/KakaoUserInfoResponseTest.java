package com.dnd.jjigeojulge.auth.infra.kakao.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.jjigeojulge.auth.domain.OAuthUserProfile;
import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class KakaoUserInfoResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("카카오 유저 정보 JSON을 파싱하고 프로필 이미지 URL을 정상적으로 추출한다.")
    void parseKakaoUserInfoAndExtractProfileImageUrl() throws JsonProcessingException {
        // given
        String json = """
                {
                  "id": 123456789,
                  "kakao_account": {
                    "profile": {
                      "profile_image_url": "http://img1.kakaocdn.net/thumb/123456789.jpg"
                    }
                  }
                }
                """;

        // when
        KakaoUserInfoResponse response = objectMapper.readValue(json, KakaoUserInfoResponse.class);
        OAuthUserProfile profile = response.toOAuthUserProfile();

        // then
        assertThat(response.id()).isEqualTo(123456789L);
        assertThat(profile.providerId()).isEqualTo("123456789");
        assertThat(profile.provider()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(profile.profileImageUrl()).isEqualTo("http://img1.kakaocdn.net/thumb/123456789.jpg");
    }

    @Test
    @DisplayName("카카오 유저 정보에서 영문이나 프로필 이미지가 없는 경우 null로 처리한다.")
    void parseKakaoUserInfoWithoutProfileImage() throws JsonProcessingException {
        // given
        String json = """
                {
                  "id": 987654321
                }
                """;

        // when
        KakaoUserInfoResponse response = objectMapper.readValue(json, KakaoUserInfoResponse.class);
        OAuthUserProfile profile = response.toOAuthUserProfile();

        // then
        assertThat(response.id()).isEqualTo(987654321L);
        assertThat(profile.profileImageUrl()).isNull();
    }
}
