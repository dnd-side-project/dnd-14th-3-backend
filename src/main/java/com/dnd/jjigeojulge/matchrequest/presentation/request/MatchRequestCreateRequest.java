package com.dnd.jjigeojulge.matchrequest.presentation.request;

import com.dnd.jjigeojulge.global.common.dto.GeoPoint;
import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "실시간 동행 매칭 대기(요청) 생성 요청")
public record MatchRequestCreateRequest(
	@Schema(description = "현재 위치 좌표")
	@NotNull(message = "위치 좌표는 필수입니다.")
	@Valid
	GeoPoint location,

	@Schema(
		description = "구체적인 장소명 또는 도로명 주소",
		example = "서울 구로구 고척로 21 고척스카이돔 1번 출입구 앞"
	)
	@NotBlank(message = "구체적인 장소는 필수입니다.")
	@Size(max = 100, message = "구체적인 장소는 최대 100자까지 가능합니다.")
	String specificPlace,

	@Schema(description = "요청 메시지", example = "고척 입구에서 전신 사진을 찍고 싶어요.")
	@Size(max = 300, message = "요청 메시지는 최대 300자까지 가능합니다.")
	String requestMessage,

	@Schema(description = "예상 촬영 소요 시간", example = "TEN_MINUTES")
	@NotNull(message = "예상 촬영 소요 시간은 필수입니다.")
	ShootingDurationOption expectedDuration
) {
}
