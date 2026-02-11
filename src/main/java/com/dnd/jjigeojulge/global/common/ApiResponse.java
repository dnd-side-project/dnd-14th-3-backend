package com.dnd.jjigeojulge.global.common;

import com.dnd.jjigeojulge.global.exception.ErrorCode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ApiResponse<T> {
	@Schema(description = "요청이 성공했는지 여부", requiredMode = Schema.RequiredMode.REQUIRED)
	private final boolean success;
	@Schema(description = "비즈니스 메시지", requiredMode = Schema.RequiredMode.REQUIRED)
	private final String message;
	@Schema(description = "에러 코드", requiredMode = Schema.RequiredMode.AUTO)
	private final String code;
	@Schema(description = "요청에 따른 응답 데이터", requiredMode = Schema.RequiredMode.AUTO)
	private final T data;

	public static <T> ApiResponse<T> of(boolean success, String message, T data) {
		return ApiResponse.<T>builder().success(success).message(message).data(data).build();
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return of(true, message, data);
	}

	public static <T> ApiResponse<T> success(T data) {
		return success("요청에 성공했습니다.", data);
	}

	public static <T> ApiResponse<T> failure(ErrorCode errorCode) {
		return ApiResponse.<T>builder()
			.success(false)
			.message(errorCode.getMessage())
			.code(errorCode.name())
			.build();
	}

}
