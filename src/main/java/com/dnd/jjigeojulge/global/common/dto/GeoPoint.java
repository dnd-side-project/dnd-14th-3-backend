package com.dnd.jjigeojulge.global.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Schema(description = "위도/경도")
public record GeoPoint(
	@Schema(description = "위도 (-90 ~ 90)", example = "37.4979")
	@NotNull(message = "위도는 필수입니다.")
	@DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
	@DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
	Double latitude,

	@Schema(description = "경도 (-180 ~ 180)", example = "127.0276")
	@NotNull(message = "경도는 필수입니다.")
	@DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
	@DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
	Double longitude
) {
}
