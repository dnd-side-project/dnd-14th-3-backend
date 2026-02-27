package com.dnd.jjigeojulge.reservation.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentContent {

    public static final int MAX_LENGTH = 500;

    @Column(name = "comment_content", length = MAX_LENGTH, nullable = false)
    private String value;

    private CommentContent(String value) {
        validate(value);
        this.value = value;
    }

    public static CommentContent from(String value) {
        return new CommentContent(value);
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다.");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("댓글 내용은 " + MAX_LENGTH + "자 이하여야 합니다.");
        }
    }
}
