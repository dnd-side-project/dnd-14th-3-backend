package com.dnd.jjigeojulge.reservation.application.dto.query;

import com.dnd.jjigeojulge.reservation.domain.ApplicantStatus;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class ApplicantInfoDto {
    private Long applicantId;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private Integer trustScore;
    private boolean hasRecentNoShow;
    private ApplicantStatus status;

    @QueryProjection
    public ApplicantInfoDto(
            Long applicantId,
            Long userId,
            String nickname,
            String profileImageUrl,
            Integer trustScore,
            boolean hasRecentNoShow,
            ApplicantStatus status) {
        this.applicantId = applicantId;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.trustScore = trustScore;
        this.hasRecentNoShow = hasRecentNoShow;
        this.status = status;
    }
}
