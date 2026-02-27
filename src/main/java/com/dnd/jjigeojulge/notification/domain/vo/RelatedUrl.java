package com.dnd.jjigeojulge.notification.domain.vo;

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
public class RelatedUrl {

    private static final int MAX_LENGTH = 255;

    @Column(name = "related_url", length = MAX_LENGTH, nullable = false)
    private String value;

    private RelatedUrl(String value) {
        this.value = value;
    }

    public static RelatedUrl from(String value) {
        String normalized = value == null ? null : value.trim();
        validate(normalized);
        return new RelatedUrl(normalized);
    }

    private static void validate(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("관련 URL은 필수입니다.");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("관련 URL은 " + MAX_LENGTH + "자를 초과할 수 없습니다.");
        }
        if (!value.startsWith("/")) {
            throw new IllegalArgumentException("관련 URL은 '/'로 시작하는 유효한 경로여야 합니다.");
        }
    }
}
