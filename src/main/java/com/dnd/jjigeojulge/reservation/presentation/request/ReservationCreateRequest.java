package com.dnd.jjigeojulge.reservation.presentation.request;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.domain.common.ShootingDurationOption;
import com.dnd.jjigeojulge.global.common.dto.GeoPoint;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "동행 예약 생성 요청")
public record ReservationCreateRequest(
	@Schema(description = "구체적인 장소(상세 주소/랜드마크)", example = "강남역 10번 출구 앞")
	@NotBlank(message = "구체적인 장소는 필수입니다.")
	String specificPlace,

	@NotNull(message = "위치 좌표는 필수입니다.")
	@Valid
	GeoPoint location,

	@Schema(description = "예약 일시 (서버 기준 KST로 해석). ISO-8601", example = "2026-02-20T14:30:00")
	@NotNull(message = "예약 일시는 필수입니다.")
	LocalDateTime scheduledAt,

	@Schema(description = "촬영 소요 시간 옵션", example = "TWENTY_MINUTES")
	@NotNull(message = "촬영 소요 시간은 필수입니다.")
	ShootingDurationOption shootingDuration,

	@Schema(description = "요청 메시지", example = "인생샷 위주로 자연스럽게 부탁드려요.", nullable = true)
	@Size(max = 500, message = "요청 메시지는 500자 이하여야 합니다.")
	String requestMessage
) {

}
