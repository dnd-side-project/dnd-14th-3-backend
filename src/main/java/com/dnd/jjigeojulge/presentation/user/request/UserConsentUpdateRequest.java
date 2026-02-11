package com.dnd.jjigeojulge.presentation.user.request;

public record UserConsentUpdateRequest(
	Boolean notificationAllowed,
	Boolean locationAllowed
) {
}
