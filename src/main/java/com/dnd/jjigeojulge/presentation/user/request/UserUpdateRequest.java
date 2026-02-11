package com.dnd.jjigeojulge.presentation.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
	@Size(min = 2, max = 10, message = "사용자 이름은 2자 이상 10자 이하여야 합니다")
	@NotBlank(message = "사용자 이름은 비어 있을 수 없습니다")
	String newUsername
) {
}
