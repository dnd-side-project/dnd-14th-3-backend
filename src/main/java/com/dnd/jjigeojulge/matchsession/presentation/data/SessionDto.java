package com.dnd.jjigeojulge.matchsession.presentation.data;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.dto.GeoPoint;
import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequestStatus;
import com.dnd.jjigeojulge.matchsession.domain.MatchSessionStatus;
import com.dnd.jjigeojulge.user.domain.Gender;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "매칭 세션 조회 응답")
public record SessionDto(

	@Schema(description = "세션 ID", example = "12")
	Long id,

	@Schema(description = "세션 상태", example = "ACTIVE")
	MatchSessionStatus status,

	GeoPoint destination,

	@Schema(description = "매칭 성사 시각 (KST)", example = "2026-02-27T01:10:30")
	LocalDateTime matchedAt,

	@Schema(description = "세션 종료 시각 (KST)", example = "2026-02-27T01:40:30", nullable = true)
	LocalDateTime endedAt,

	@Schema(description = "현재 로그인 사용자 정보")
	UserSummaryDto me,

	@Schema(description = "상대 사용자 정보")
	UserSummaryDto partner
) {

	@Schema(description = "세션 참여자 정보")
	public record UserSummaryDto(
		@Schema(description = "유저 ID", example = "3")
		Long userId,

		@Schema(description = "닉네임", example = "곰곰")
		String nickname,

		@Schema(description = "성별", example = "MALE")
		Gender gender,

		@Schema(description = "도착 여부", example = "false")
		boolean arrived,

		@Schema(description = "해당 유저의 매칭 요청 요약 정보")
		MatchRequestSummaryDto request
		// TODO 나이, 점수 추가
	) {
	}

	@Schema(description = "매칭 요청 요약 정보")
	public record MatchRequestSummaryDto(
		@Schema(description = "매칭 요청 ID", example = "135")
		Long matchRequestId,

		@Schema(description = "매칭 요청 상태", example = "WAITING")
		MatchRequestStatus status,

		@Schema(
			description = "구체적인 장소명 또는 도로명 주소",
			example = "서울 구로구 고척로 21 고척스카이돔 1번 출입구 앞"
		)
		String specificPlace,

		@Schema(description = "요청 메시지", example = "고척 입구에서 전신 사진을 찍고 싶어요.")
		String requestMessage,

		@Schema(description = "예상 촬영 소요 시간", example = "TEN_MINUTES")
		ShootingDurationOption expectedDuration

	) {
	}
}
