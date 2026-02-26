package com.dnd.jjigeojulge.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

class IntroductionTest {

    @Test
    @DisplayName("50자 이하의 정상적인 한줄소개를 생성할 수 있다.")
    void createValidIntroduction() {
        // given
        String validText = "안녕하세요! 반갑습니다. 취미로 사진을 찍습니다.";

        // when
        Introduction introduction = Introduction.from(validText);

        // then
        assertThat(introduction.getValue()).isEqualTo(validText);
    }

    @Test
    @DisplayName("한줄소개가 null이거나 빈 문자열이면 내부 값이 null인 객체를 반환한다.")
    void createEmptyIntroduction() {
        // when
        Introduction nullIntro = Introduction.from(null);
        Introduction emptyIntro = Introduction.from("   ");

        // then
        assertThat(nullIntro.getValue()).isNull();
        assertThat(emptyIntro.getValue()).isNull();
    }

    @Test
    @DisplayName("한줄소개가 50자를 초과하면 예외가 발생한다.")
    void throwExceptionWhenExceedingLength() {
        // given
        String longText = "가".repeat(51);

        // when & then
        assertThatThrownBy(() -> Introduction.from(longText))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_PROFILE_REQUEST.getMessage());
    }
}
