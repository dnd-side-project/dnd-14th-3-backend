package com.dnd.jjigeojulge.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// ===== 400 Bad Request =====
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "요청 파라미터가 올바르지 않습니다."),
	VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "요청 값 검증에 실패했습니다."),
	CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST, "요청 값이 제약 조건을 위반했습니다."),
	MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),

	INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "닉네임은 2~20자이며 한글, 영문, 숫자만 사용할 수 있습니다."),
	INVALID_PROFILE_REQUEST(HttpStatus.BAD_REQUEST, "요청한 프로필 정보가 올바르지 않습니다."),
	// ===== 404 Not Found =====
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User를 찾을 수 없습니다."),

	// ===== 405 Method Not Allowed =====
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),

	// ===== 415 Unsupported Media Type =====
	UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 타입입니다."),

	// ===== 409 Conflict =====
	CONFLICT(HttpStatus.CONFLICT, "요청이 현재 서버 상태와 충돌합니다."),

	// ===== 500 Internal Server Error =====
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.");

	private final HttpStatus status;
	private final String message;

}
