package com.dnd.jjigeojulge.presentation.user.request;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
	@Size(min = 2, max = 50, message = "사용자 이름은 3자 이상 50자 이하여야 합니다")
	String newUsername
) {
}
