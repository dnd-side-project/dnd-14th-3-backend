package com.dnd.jjigeojulge.reservation.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.jjigeojulge.reservation.application.dto.AddCommentCommand;
import com.dnd.jjigeojulge.reservation.application.dto.UpdateCommentCommand;
import com.dnd.jjigeojulge.reservation.domain.ReservationComment;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationCommentRepository;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationRepository;
import com.dnd.jjigeojulge.user.infra.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReservationCommentServiceTest {

    @Mock
    private ReservationCommentRepository reservationCommentRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationCommentService reservationCommentService;

    @Test
    @DisplayName("댓글을 성공적으로 작성한다.")
    void addComment_Success() {
        // given
        Long reservationId = 1L;
        Long authorId = 2L;
        AddCommentCommand command = new AddCommentCommand(reservationId, authorId, "테스트 댓글");

        given(reservationRepository.findById(reservationId))
                .willReturn(Optional.of(mock(com.dnd.jjigeojulge.reservation.domain.Reservation.class)));
        given(userRepository.existsById(authorId)).willReturn(true);
        given(reservationCommentRepository.save(any(ReservationComment.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        Long commentId = reservationCommentService.addComment(command);

        // then
        verify(reservationCommentRepository).save(any(ReservationComment.class));
    }

    @Test
    @DisplayName("작성자 본인인 경우 댓글을 수정한다.")
    void updateComment_Success() {
        // given
        Long commentId = 1L;
        Long userId = 2L;
        UpdateCommentCommand command = new UpdateCommentCommand(commentId, userId, "수정된 내용");
        ReservationComment comment = mock(ReservationComment.class);

        given(reservationCommentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        reservationCommentService.updateComment(command);

        // then
        verify(comment).updateContent(eq(userId), any());
    }

    @Test
    @DisplayName("작성자 본인인 경우 댓글을 삭제한다.")
    void deleteComment_Success() {
        // given
        Long commentId = 1L;
        Long userId = 2L;
        ReservationComment comment = mock(ReservationComment.class);

        given(reservationCommentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        reservationCommentService.deleteComment(commentId, userId);

        // then
        verify(comment).delete(userId);
    }

    @Test
    @DisplayName("작성자가 아닌 사람이 삭제를 시도하면 예외가 발생한다.")
    void deleteComment_Fail_NotAuthor() {
        // given
        Long commentId = 1L;
        Long userId = 2L;
        ReservationComment comment = mock(ReservationComment.class);

        given(reservationCommentRepository.findById(commentId)).willReturn(Optional.of(comment));
        willThrow(new IllegalArgumentException("댓글 작성자 본인만 삭제할 수 있습니다."))
                .given(comment).delete(userId);

        // when & then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> reservationCommentService.deleteComment(commentId, userId))
                .withMessage("댓글 작성자 본인만 삭제할 수 있습니다.");
    }
}
