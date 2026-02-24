package com.dnd.jjigeojulge.matchproposal.data;

import com.dnd.jjigeojulge.matchproposal.domain.MatchDecisionStatus;
import com.dnd.jjigeojulge.matchproposal.domain.MatchProposal;
import com.dnd.jjigeojulge.matchproposal.domain.MatchProposalStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "매칭 제안 상태 조회 응답")
public record MatchProposalDto(

	@Schema(description = "매칭 제안 ID", example = "12")
	Long id,

	@Schema(description = "매칭 제안 유저 A ID", example = "3")
	Long userAId,

	@Schema(description = "매칭 제안 유저 B ID", example = "4")
	Long userBId,

	@Schema(description = "매칭 제안 전체 상태", example = "ACCEPTED")
	MatchProposalStatus status,

	@Schema(description = "유저 A 매칭 수락 상태", example = "ACCEPTED")
	MatchDecisionStatus userADecision,

	@Schema(description = "유저 B 매칭 수락 상태", example = "ACCEPTED")
	MatchDecisionStatus userBDecision
) {

	public static MatchProposalDto from(MatchProposal matchProposal) {
		return new MatchProposalDto(
			matchProposal.getId(),
			matchProposal.getUserAId(),
			matchProposal.getUserBId(),
			matchProposal.getStatus(),
			matchProposal.getADecision(),
			matchProposal.getBDecision()
		);
	}
}
