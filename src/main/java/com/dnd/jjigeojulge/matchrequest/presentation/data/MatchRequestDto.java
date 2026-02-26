package com.dnd.jjigeojulge.matchrequest.presentation.data;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.dto.GeoPoint;
import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequest;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequestStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "매칭 대기(요청) 상태 조회 응답")
public record MatchRequestDto(

	@Schema(description = "매칭 요청 ID", example = "135")
	Long matchRequestId,

	@Schema(description = "매칭 요청 상태", example = "WAITING")
	MatchRequestStatus status,

	@Schema(
		description = "구체적인 장소명 또는 도로명 주소",
		example = "서울 구로구 고척로 21 고척스카이돔 1번 출입구 앞"
	)
	String specificPlace,

	GeoPoint location,

	@Schema(description = "예상 촬영 소요 시간", example = "TEN_MINUTES")
	ShootingDurationOption expectedDuration,

	@Schema(description = "요청 메시지", example = "고척 입구에서 전신 사진을 찍고 싶어요.")
	String requestMessage,

	@Schema(description = "생성 시각(KST)", example = "2026-02-17T09:10:00")
	LocalDateTime createdAt,

	@Schema(description = "수정 시각(KST)", example = "2026-02-17T09:15:00")
	LocalDateTime updatedAt,

	@Schema(description = "현재 대기중인 사람 수", example = "3")
	int nearbyWaitingCount
) {

	public static MatchRequestDto from(MatchRequest entity, int nearbyWaitingCount) {
		GeoPoint location = new GeoPoint(
			entity.getLatitude().doubleValue(),
			entity.getLongitude().doubleValue()
		);
		return new MatchRequestDto(
			entity.getId(),
			entity.getStatus(),
			entity.getSpecificPlace(),
			location,
			entity.getExpectedDuration(),
			entity.getRequestMessage(),
			entity.getCreatedAt(),
			entity.getUpdatedAt(),
			nearbyWaitingCount
		);
	}
}
