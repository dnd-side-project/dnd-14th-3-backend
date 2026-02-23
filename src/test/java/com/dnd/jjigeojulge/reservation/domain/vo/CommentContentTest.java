package com.dnd.jjigeojulge.reservation.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class CommentContentTest {

    @Test
    @DisplayName("500자 이하의 댓글 내용은 정상적으로 생성된다.")
    void create_Success() {
        String validContent = "이 예약에 대해 궁금한 점이 있어요!";
        CommentContent content = CommentContent.from(validContent);
        assertThat(content.getValue()).isEqualTo(validContent);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("댓글 내용이 비어있으면 예외가 발생한다.")
    void create_Fail_Empty(String emptyContent) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> CommentContent.from(emptyContent))
                .withMessage("댓글 내용은 비어있을 수 없습니다.");
    }

    @Test
    @DisplayName("500자를 초과하는 댓글 내용은 예외가 발생한다.")
    void create_Fail_TooLong() {
        String longContent = "a".repeat(501);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> CommentContent.from(longContent))
                .withMessage("댓글 내용은 500자 이하여야 합니다.");
    }
}
