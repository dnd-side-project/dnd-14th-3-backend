package com.dnd.jjigeojulge.user.presentation.request;

public record UserConsentUpdateRequest(
	Boolean notificationAllowed,
	Boolean locationAllowed
) {
}
