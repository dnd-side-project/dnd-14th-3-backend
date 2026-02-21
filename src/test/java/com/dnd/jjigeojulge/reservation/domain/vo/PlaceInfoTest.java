package com.dnd.jjigeojulge.reservation.domain.vo;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class PlaceInfoTest {

	@Test
	@DisplayName("올바른 장소 이름과 좌표가 주어지면 정상 생성된다")
	void create_Success() {
		// given
		String placeName = "강남역 10번 출구 앞";
		Double lat = 37.4979;
		Double lng = 127.0276;

		// when
		PlaceInfo placeInfo = PlaceInfo.of(placeName, lat, lng);

		// then
		assertThat(placeInfo.getSpecificPlace()).isEqualTo(placeName);
		assertThat(placeInfo.getLatitude()).isEqualTo(BigDecimal.valueOf(lat));
		assertThat(placeInfo.getLongitude()).isEqualTo(BigDecimal.valueOf(lng));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("장소 이름이 비어있으면 예외가 발생한다")
	void create_Fail_EmptyPlaceName(String emptyPlaceName) {
		// when & then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> PlaceInfo.of(emptyPlaceName, 37.0, 127.0))
			.withMessage("구체적인 장소 이름은 필수입니다.");
	}

	@Test
	@DisplayName("위도나 경도가 null이면 예외가 발생한다")
	void create_Fail_NullCoordinates() {
		// when & then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> PlaceInfo.of("강남역", null, 127.0))
			.withMessage("좌표(위도/경도)는 필수입니다.");

		assertThatIllegalArgumentException()
			.isThrownBy(() -> PlaceInfo.of("강남역", 37.0, null))
			.withMessage("좌표(위도/경도)는 필수입니다.");
	}
}
