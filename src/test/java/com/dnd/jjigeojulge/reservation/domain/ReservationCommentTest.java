package com.dnd.jjigeojulge.reservation.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.jjigeojulge.reservation.domain.vo.CommentContent;

class ReservationCommentTest {

    @Test
    @DisplayName("올바른 정보로 댓글을 생성할 수 있다.")
    void create_Success() {
        Long reservationId = 100L;
        Long authorId = 1L;
        CommentContent content = CommentContent.from("안녕하세요");

        ReservationComment comment = ReservationComment.create(reservationId, authorId, content);

        assertThat(comment.getReservationId()).isEqualTo(reservationId);
        assertThat(comment.getAuthorId()).isEqualTo(authorId);
        assertThat(comment.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("댓글 작성자 본인인지 확인한다.")
    void isAuthor_Check() {
        ReservationComment comment = ReservationComment.create(100L, 1L, CommentContent.from("테스트"));

        assertThat(comment.isAuthor(1L)).isTrue();
        assertThat(comment.isAuthor(2L)).isFalse();
    }

    @Test
    @DisplayName("작성자 본인인 경우 내용을 수정할 수 있다.")
    void updateContent_Success() {
        ReservationComment comment = ReservationComment.create(100L, 1L, CommentContent.from("기존 내용"));
        CommentContent newContent = CommentContent.from("수정된 내용");

        comment.updateContent(1L, newContent);

        assertThat(comment.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("작성자 본인이 아니면 내용을 수정할 수 없다.")
    void updateContent_Fail_NotAuthor() {
        ReservationComment comment = ReservationComment.create(100L, 1L, CommentContent.from("기존 내용"));
        CommentContent newContent = CommentContent.from("수정된 내용");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> comment.updateContent(2L, newContent))
                .withMessage("댓글 작성자 본인만 수정할 수 있습니다.");
    }
}
