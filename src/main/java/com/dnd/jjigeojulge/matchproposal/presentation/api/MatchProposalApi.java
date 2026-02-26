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
			- 수락 시 상대방에게 서버가 `match.proposal.accepted` SSE 이벤트를 발송합니다.
			- 양쪽 모두 수락하면 제안 상태가 `ACCEPTED`가 되며, 서버는 이후 `match.session` SSE 이벤트를 발송합니다.
			"""
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "수락 성공"
		),

		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "403", description = "참여자가 아님",
			content = @Content(examples = @ExampleObject(value = """
				{
				  "success": false,
				  "message": "해당 매칭 제안의 참여자가 아닙니다.",
				  "code": "MATCH_PROPOSAL_NOT_PARTICIPANT",
				  "data": null
				}
				"""))
		),

		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "409", description = "상태 충돌 (이미 결정/상대 거절/이미 처리됨)",
			content = @Content(examples = {
				@ExampleObject(name = "AlreadyDecided", value = """
					{
					  "success": false,
					  "message": "이미 해당 매칭 제안에 대해 결정을 완료했습니다.",
					  "code": "MATCH_PROPOSAL_ALREADY_DECIDED",
					  "data": null
					}
					"""),
				@ExampleObject(name = "RejectedByOpponent", value = """
					{
					  "success": false,
					  "message": "상대방이 이미 매칭 제안을 거절했습니다.",
					  "code": "MATCH_PROPOSAL_REJECTED_BY_OPPONENT",
					  "data": null
					}
					"""),
				@ExampleObject(name = "AlreadyProcessed", value = """
					{
					  "success": false,
					  "message": "이미 처리된 매칭 제안입니다.",
					  "code": "MATCH_PROPOSAL_ALREADY_PROCESSED",
					  "data": null
					}
					""")
			})
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
			- 거절 시 상대방에게 서버가 `match.proposal.rejected` SSE 메시지를 발송합니다.
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
			responseCode = "403", description = "참여자가 아님",
			content = @Content(examples = @ExampleObject(value = """
				{
				  "success": false,
				  "message": "해당 매칭 제안의 참여자가 아닙니다.",
				  "code": "MATCH_PROPOSAL_NOT_PARTICIPANT",
				  "data": null
				}
				"""))
		),

		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "409", description = "상태 충돌 (이미 결정/상대 거절/이미 처리됨)",
			content = @Content(examples = {
				@ExampleObject(name = "AlreadyDecided", value = """
					{
					  "success": false,
					  "message": "이미 해당 매칭 제안에 대해 결정을 완료했습니다.",
					  "code": "MATCH_PROPOSAL_ALREADY_DECIDED",
					  "data": null
					}
					"""),
				@ExampleObject(name = "RejectedByOpponent", value = """
					{
					  "success": false,
					  "message": "상대방이 이미 매칭 제안을 거절했습니다.",
					  "code": "MATCH_PROPOSAL_REJECTED_BY_OPPONENT",
					  "data": null
					}
					"""),
				@ExampleObject(name = "AlreadyProcessed", value = """
					{
					  "success": false,
					  "message": "이미 처리된 매칭 제안입니다.",
					  "code": "MATCH_PROPOSAL_ALREADY_PROCESSED",
					  "data": null
					}
					""")
			})
		)

	})
	ResponseEntity<ApiResponse<MatchProposalDto>> reject(
		@Parameter(description = "매칭 제안 ID", example = "123", required = true)
		Long proposalId,
		@Parameter(hidden = true)
		CustomUserDetails userDetails
	);
}
