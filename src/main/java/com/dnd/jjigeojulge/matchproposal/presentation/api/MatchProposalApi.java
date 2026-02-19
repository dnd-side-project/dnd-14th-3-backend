package com.dnd.jjigeojulge.matchproposal.presentation.api;

import org.springframework.http.ResponseEntity;

import com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails;
import com.dnd.jjigeojulge.global.common.response.ApiResponse;
import com.dnd.jjigeojulge.matchproposal.data.MatchProposalDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "매칭 제안 결정", description = "매칭 제안(Proposal)에 대해 수락/거절 결정을 내리는 API")
public interface MatchProposalApi {

	@Operation(
		summary = "매칭 제안 수락",
		description = """
			매칭 제안을 수락합니다.
			
			- 인증된 사용자가 A/B 중 누구인지 서버가 판별합니다.
			- 한쪽만 수락하면 제안 상태는 `PENDING` 유지, 해당 사용자 decision만 `ACCEPTED`로 변경됩니다.
			- 양쪽 모두 수락하면 제안 상태가 `ACCEPTED`가 되며, 서버는 이후 `match.confirmed` SSE 이벤트를 발송합니다.
			"""
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "수락 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "매칭 제안을 찾을 수 없음",
			content = @Content(examples = @ExampleObject(value = """
				{
					"success" : false,
					"message" : "매칭 제안을 찾을 수 없습니다.",
					"code" : "MATCH_PROPOSAL_NOT_FOUND",
					"data" : null
				}
				"""))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400", description = "잘못된 요청 (참여자가 아니거나 이미 처리된 제안)",
			content = @Content(examples = @ExampleObject(value = """
				{
				   "success" : false,
				   "message" : "해당 매칭 제안에 참여한 유저가 아닙니다.",
				   "code" : "INVALID_MATCH_PARTICIPANT",
				   "data" : null
				}
				"""))
		)
	})
	ResponseEntity<ApiResponse<MatchProposalDto>> accept(
		@Parameter(description = "매칭 제안 ID", example = "123", required = true)
		Long proposalId,
		@Parameter(hidden = true)
		CustomUserDetails userDetails
	);

	@Operation(
		summary = "매칭 제안 거절",
		description = """
			매칭 제안을 거절합니다.
			
			- 참여자 중 한 명이라도 거절하면 전체 제안 상태(`status`)는 즉시 `REJECTED`로 변경됩니다.
			- 이미 결정이 완료된 제안에 대해서는 수정이 불가능할 수 있습니다.
			"""
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "거절 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "매칭 제안을 찾을 수 없음",
			content = @Content(examples = @ExampleObject(value = """
				{
				   "success" : false,
				   "message" : "매칭 제안을 찾을 수 없습니다.",
				   "code" : "MATCH_PROPOSAL_NOT_FOUND",
				   "data" : null
				}
				"""))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400", description = "잘못된 요청 (참여자가 아니거나 이미 처리된 제안)",
			content = @Content(examples = @ExampleObject(value = """
				{
				   "success" : false,
				   "message" : "해당 매칭 제안에 참여한 유저가 아닙니다.",
				   "code" : "INVALID_MATCH_PARTICIPANT",
				   "data" : null
				}
				"""))
		)
	})
	ResponseEntity<ApiResponse<MatchProposalDto>> reject(
		@Parameter(description = "매칭 제안 ID", example = "123", required = true)
		Long proposalId,
		@Parameter(hidden = true)
		CustomUserDetails userDetails
	);
}
