package com.dnd.jjigeojulge.matchproposal.domain;

/**
 * 매치 제안에 대한 개별 참여자의 의사결정 상태
 *  (제안 전체의 상태는 {`@link` MatchProposalStatus}로 관리)
 */
public enum MatchDecisionStatus {
	PENDING,
	ACCEPTED,
	REJECTED
}
