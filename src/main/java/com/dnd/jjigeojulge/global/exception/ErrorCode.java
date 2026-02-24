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
	INVALID_MATCH_PARTICIPANT(HttpStatus.BAD_REQUEST, "해당 매칭 제안에 참여한 유저가 아닙니다."),
	MATCH_PROPOSAL_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 처리된 매칭 제안입니다."),
	INVALID_PROFILE_REQUEST(HttpStatus.BAD_REQUEST, "요청한 프로필 정보가 올바르지 않습니다."),
	INVALID_PHOTO_STYLE(HttpStatus.BAD_REQUEST, "요청한 사진 스타일이 올바르지 않습니다."),
	INVALID_OAUTH_REQUEST(HttpStatus.BAD_REQUEST, "소셜 로그인 요청이 올바르지 않습니다."),

	// ===== 401 Unauthorized =====
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

	// ===== 403 Forbidden =====
	NOT_MATCH_REQUEST_OWNER(HttpStatus.FORBIDDEN, "본인의 매칭 요청만 조작할 수 있습니다."),

	// ===== 404 Not Found =====
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User를 찾을 수 없습니다."),
	MATCH_PROPOSAL_NOT_FOUND(HttpStatus.NOT_FOUND, "매칭 제안을 찾을 수 없습니다."),
	MATCH_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "매칭 요청을 찾을 수 없습니다."),
	RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다."),
	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글 정보를 찾을 수 없습니다."),

	// ===== 405 Method Not Allowed =====
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),

	// ===== 415 Unsupported Media Type =====
	UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 타입입니다."),

	// ===== 409 Conflict =====
	CONFLICT(HttpStatus.CONFLICT, "요청이 현재 서버 상태와 충돌합니다."),
	MATCH_REQUEST_ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 진행 중인 매칭 요청이 존재합니다."),
	MATCH_REQUEST_NOT_EXPIRED(HttpStatus.CONFLICT, "아직 대기 시간이 남아있어 재시도할 수 없습니다."),

	// ===== 500 Internal Server Error =====
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."),

	OAUTH_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "소셜 로그인 서버와의 통신 중 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String message;

}
