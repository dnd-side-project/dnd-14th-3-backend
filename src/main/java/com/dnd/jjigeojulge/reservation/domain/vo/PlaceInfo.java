package com.dnd.jjigeojulge.reservation.domain.vo;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceInfo {

        @Enumerated(EnumType.STRING)
        @Column(name = "region_1depth", nullable = false)
        private Region1Depth region1Depth;

        @Column(name = "specific_place", nullable = false)
        private String specificPlace;

        @Column(name = "latitude", precision = 10, scale = 6, nullable = false)
        private BigDecimal latitude;

        @Column(name = "longitude", precision = 10, scale = 6, nullable = false)
        private BigDecimal longitude;

        private PlaceInfo(Region1Depth region1Depth, String specificPlace, BigDecimal latitude, BigDecimal longitude) {
                this.region1Depth = region1Depth;
                this.specificPlace = specificPlace;
                this.latitude = latitude;
                this.longitude = longitude;
        }

        public static PlaceInfo of(String regionName, String specificPlace, Double latitude, Double longitude) {
                Region1Depth region1Depth = Region1Depth.fromLabel(regionName);
                validate(region1Depth, specificPlace, latitude, longitude);
                return new PlaceInfo(region1Depth, specificPlace, BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude));
        }

        private static void validate(Region1Depth region1Depth, String specificPlace, Double latitude, Double longitude) {
                if (region1Depth == null) {
                        throw new IllegalArgumentException("1Depth 지역 정보는 필수입니다.");
                }
                if (specificPlace == null || specificPlace.isBlank()) {
                        throw new IllegalArgumentException("구체적인 장소 이름은 필수입니다.");
                }
                if (latitude == null || longitude == null) {
                        throw new IllegalArgumentException("좌표(위도/경도)는 필수입니다.");
                }
        }
}
