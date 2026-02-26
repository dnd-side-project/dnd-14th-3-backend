package com.dnd.jjigeojulge.user.domain;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;

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
public class Introduction {

    private static final int MAX_LENGTH = 50;

    @Column(name = "introduction", length = MAX_LENGTH)
    private String value;

    private Introduction(String value) {
        this.value = value;
    }

    public static Introduction from(String value) {
        if (value == null || value.trim().isEmpty()) {
            return new Introduction(null); // 한 줄 소개는 선택값이므로 null/빈 문자열 허용
        }
        if (value.length() > MAX_LENGTH) {
            throw new BusinessException(ErrorCode.INVALID_PROFILE_REQUEST);
        }
        return new Introduction(value);
    }
}
