package com.dnd.jjigeojulge.matchproposal.domain;

import com.dnd.jjigeojulge.global.common.entity.BaseUpdatableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "match_proposals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchProposal extends BaseUpdatableEntity {

	@Column(name = "user_a_id", nullable = false)
	private Long userAId;

	@Column(name = "user_b_id", nullable = false)
	private Long userBId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private MatchProposalStatus status;

	@Enumerated(EnumType.STRING)
	@Column(name = "a_decision", nullable = false, length = 20)
	private MatchDecisionStatus aDecision;

	@Enumerated(EnumType.STRING)
	@Column(name = "b_decision", nullable = false, length = 20)
	private MatchDecisionStatus bDecision;

	@Builder
	private MatchProposal(Long userAId, Long userBId) {
		this.userAId = userAId;
		this.userBId = userBId;

		this.status = MatchProposalStatus.PENDING;
		this.aDecision = MatchDecisionStatus.PENDING;
		this.bDecision = MatchDecisionStatus.PENDING;
	}

	public static MatchProposal create(Long userAId, Long userBId) {
		if (userAId.equals(userBId)) {
			throw new IllegalArgumentException("userAId and userBId must be different");
		}
		return new MatchProposal(userAId, userBId);
	}

	public void acceptByA() {
		this.aDecision = MatchDecisionStatus.ACCEPTED;
		updateFinalStatusIfDecided();
	}

	public void rejectByA() {
		this.aDecision = MatchDecisionStatus.REJECTED;
		this.status = MatchProposalStatus.REJECTED;
	}

	public void acceptByB() {
		this.bDecision = MatchDecisionStatus.ACCEPTED;
		updateFinalStatusIfDecided();
	}

	public void rejectByB() {
		this.bDecision = MatchDecisionStatus.REJECTED;
		this.status = MatchProposalStatus.REJECTED;
	}

	private void updateFinalStatusIfDecided() {
		if (this.aDecision == MatchDecisionStatus.ACCEPTED && this.bDecision == MatchDecisionStatus.ACCEPTED) {
			this.status = MatchProposalStatus.ACCEPTED;
		}
	}
}
