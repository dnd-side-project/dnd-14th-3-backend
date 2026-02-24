package com.dnd.jjigeojulge.reservation.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.reservation.application.dto.AddCommentCommand;
import com.dnd.jjigeojulge.reservation.application.dto.UpdateCommentCommand;
import com.dnd.jjigeojulge.reservation.domain.ReservationComment;
import com.dnd.jjigeojulge.reservation.domain.exception.CommentNotFoundException;
import com.dnd.jjigeojulge.reservation.domain.exception.ReservationNotFoundException;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationCommentRepository;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationRepository;
import com.dnd.jjigeojulge.reservation.domain.vo.CommentContent;
import com.dnd.jjigeojulge.user.domain.exception.UserNotFoundException;
import com.dnd.jjigeojulge.user.infra.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommentService {

    private final ReservationCommentRepository reservationCommentRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public Long addComment(AddCommentCommand command) {
        if (!reservationRepository.findById(command.reservationId()).isPresent()) {
            throw new ReservationNotFoundException();
        }
        if (!userRepository.existsById(command.authorId())) {
            throw new UserNotFoundException();
        }

        ReservationComment comment = ReservationComment.create(
                command.reservationId(),
                command.authorId(),
                CommentContent.from(command.content())
        );

        return reservationCommentRepository.save(comment).getId();
    }

    public void updateComment(UpdateCommentCommand command) {
        ReservationComment comment = reservationCommentRepository.findById(command.commentId())
                .orElseThrow(CommentNotFoundException::new);

        comment.updateContent(command.userId(), CommentContent.from(command.content()));
    }

    public void deleteComment(Long commentId, Long userId) {
        ReservationComment comment = reservationCommentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.isAuthor(userId)) {
            throw new IllegalArgumentException("댓글 작성자 본인만 삭제할 수 있습니다.");
        }

        reservationCommentRepository.delete(comment);
    }
}
