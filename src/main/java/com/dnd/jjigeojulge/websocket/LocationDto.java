package com.dnd.jjigeojulge.websocket;

import java.time.LocalDateTime;

public record LocationDto(
	Long userId,
	Double latitude,  // 위도
	Double longitude, // 경도
	LocalDateTime timestamp
) {

	public static LocationDto of(Long userId, Double latitude, Double longitude) {
		return new LocationDto(
			userId,
			latitude,
			longitude,
			LocalDateTime.now()
		);
	}
}
