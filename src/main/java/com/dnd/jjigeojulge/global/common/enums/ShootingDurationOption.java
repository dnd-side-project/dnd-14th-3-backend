package com.dnd.jjigeojulge.global.common.enums;

public enum ShootingDurationOption {
	TEN_MINUTES(10),
	TWENTY_MINUTES(20),
	THIRTY_PLUS_MINUTES(30);

	private final int minMinutes;

	ShootingDurationOption(int minMinutes) {
		this.minMinutes = minMinutes;
	}

	public int minMinutes() {
		return minMinutes;
	}
}
