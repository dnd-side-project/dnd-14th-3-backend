package com.dnd.jjigeojulge.matchsession.presentation.api;

import org.springframework.http.ResponseEntity;

import com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails;
import com.dnd.jjigeojulge.global.common.response.ApiResponse;
import com.dnd.jjigeojulge.matchsession.presentation.data.SessionDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "매칭 세션 관리",
	description = """
		매칭이 성사된 이후 생성되는 세션을 관리합니다.
		세션 조회, 상태 확인, 종료 등의 기능을 제공합니다.""")
public interface MatchSessionApi {

	@Operation(
		summary = "매칭 세션 조회",
		description = "세션 ID로 매칭 세션을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "조회 성공"
		),

		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401", description = "인증 실패(토큰 누락/만료)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "인증이 필요합니다.",
					  "code": "UNAUTHORIZED",
					  "data": null
					}
					""")
			)
		),

		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "403",
			description = "참가하지 않은 유저",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "해당 매칭 세션의 참여자가 아닙니다.",
					  "code": "MATCH_SESSION_NOT_PARTICIPANT",
					  "data": null
					}
					""")
			)
		),

		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "매칭 세션 요청을 찾을 수 없음",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "매칭 세션을 찾을 수 없습니다.",
					  "code": "MATCH_SESSION_NOT_FOUND",
					  "data": null
					}
					""")
			)
		),

	})
	ResponseEntity<ApiResponse<SessionDto>> getMatchSession(
		@Parameter(description = "조회할 매칭 세션 ID", required = true, example = "12") Long sessionId,
		@Parameter(hidden = true) CustomUserDetails userDetails
	);

	@Operation(summary = "도착 완료 처리",
		description = """
				현재 로그인 사용자의 도착 상태를 완료로 변경합니다.
				두 사용자 모두 도착하면 세션 상태가 ARRIVED로 변경됩니다.
				세션 연결된 웹소켓으로 메시지를 브로드캐스트 합니다.
			""")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "204", description = "도착 처리 성공"
		),

		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "403",
			description = "참가하지 않은 유저",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "해당 매칭 세션의 참여자가 아닙니다.",
					  "code": "MATCH_SESSION_NOT_PARTICIPANT",
					  "data": null
					}
					""")
			)
		),

		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "매칭 요청을 찾을 수 없음",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "매칭 세션을 찾을 수 없습니다.",
					  "code": "MATCH_SESSION_NOT_FOUND",
					  "data": null
					}
					""")
			))
	})
	ResponseEntity<Void> arrive(
		@Parameter(description = "매칭 세션 ID", required = true, example = "12") Long sessionId,
		@Parameter(hidden = true) CustomUserDetails userDetails
	);
}
