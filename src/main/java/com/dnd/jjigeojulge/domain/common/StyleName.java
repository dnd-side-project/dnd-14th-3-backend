package com.dnd.jjigeojulge.domain.common;

import lombok.Getter;

@Getter
public enum StyleName {

	UPPER_BODY_FOCUS("상반신 위주"),
	FULL_BODY("전신 촬영"),
	PROP_USAGE("소품 활용"),
	INDOOR_SHOOT("실내 촬영(카페 등)"),
	OUTDOOR_NATURAL_LIGHT("야외 촬영(자연광 선호)"),
	SNS_UPLOAD("SNS 업로드용"),
	PERSONAL_RECORD("개인 기록용");

	private final String label;

	StyleName(String label) {
		this.label = label;
	}

	public static StyleName fromLabel(String label) {
		for (StyleName value : values()) {
			if (value.label.equals(label)) {
				return value;
			}
		}
		throw new IllegalArgumentException("Invalid style label: " + label);
	}
}
