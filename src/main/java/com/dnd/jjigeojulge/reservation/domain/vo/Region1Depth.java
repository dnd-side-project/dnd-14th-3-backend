package com.dnd.jjigeojulge.reservation.domain.vo;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Region1Depth {
        SEOUL("서울특별시"),
        GYEONGGI("경기도"),
        INCHEON("인천광역시"),
        GANGWON("강원특별자치도"),
        CHUNGBUK("충청북도"),
        CHUNGNAM("충청남도"),
        DAEJEON("대전광역시"),
        SEJONG("세종특별자치시"),
        JEONBUK("전북특별자치도"),
        JEONNAM("전라남도"),
        GWANGJU("광주광역시"),
        GYEONGBUK("경상북도"),
        GYEONGNAM("경상남도"),
        DAEGU("대구광역시"),
        ULSAN("울산광역시"),
        BUSAN("부산광역시"),
        JEJU("제주특별자치도");

        private final String label;

        @JsonValue
        public String getLabel() {
                return this.label;
        }

        @JsonCreator
        public static Region1Depth fromLabel(String label) {
                return Arrays.stream(values())
                                .filter(region -> region.label.equals(label))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 지역입니다: " + label));
        }
}
