package com.dnd.jjigeojulge.presentation.user.data;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.user.domain.UserSetting;
import com.fasterxml.jackson.annotation.JsonFormat;

public record ConsentDto(
	boolean notificationAllowed,
	boolean locationAllowed,
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime updatedAt
) {

	public static ConsentDto from(UserSetting userSetting) {
		return new ConsentDto(
			userSetting.isNotificationEnabled(),
			userSetting.isLocationSharingEnabled(),
			userSetting.getUpdatedAt()
		);
	}
}
