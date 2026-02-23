package com.dnd.jjigeojulge.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.dnd.jjigeojulge.global.common.entity.BaseUpdatableEntity;
import com.dnd.jjigeojulge.reservation.domain.vo.CommentContent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reservation_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationComment extends BaseUpdatableEntity {

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Embedded
    private CommentContent content;

    private ReservationComment(Long reservationId, Long authorId, CommentContent content) {
        validate(reservationId, authorId, content);
        this.reservationId = reservationId;
        this.authorId = authorId;
        this.content = content;
    }

    public static ReservationComment create(Long reservationId, Long authorId, CommentContent content) {
        return new ReservationComment(reservationId, authorId, content);
    }

    private static void validate(Long reservationId, Long authorId, CommentContent content) {
        if (reservationId == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }
        if (authorId == null) {
            throw new IllegalArgumentException("작성자 ID는 필수입니다.");
        }
        if (content == null) {
            throw new IllegalArgumentException("댓글 내용은 필수입니다.");
        }
    }

    public void updateContent(Long requesterId, CommentContent newContent) {
        if (!isAuthor(requesterId)) {
            throw new IllegalArgumentException("댓글 작성자 본인만 수정할 수 있습니다.");
        }
        this.content = newContent;
    }

    public boolean isAuthor(Long userId) {
        return this.authorId.equals(userId);
    }
}
