package com.dnd.jjigeojulge.matchproposal.domain;

import com.dnd.jjigeojulge.global.common.entity.BaseUpdatableEntity;
import com.dnd.jjigeojulge.matchproposal.exception.MatchProposalAlreadyDecidedException;
import com.dnd.jjigeojulge.matchproposal.exception.MatchProposalAlreadyProcessedException;
import com.dnd.jjigeojulge.matchproposal.exception.MatchProposalAlreadyRejectedException;
import com.dnd.jjigeojulge.matchproposal.exception.MatchProposalNotParticipantException;

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
		if (userAId == null || userBId == null) {
			throw new IllegalArgumentException("userAId and userBId must not be null");
		}
		if (userAId.equals(userBId)) {
			throw new IllegalArgumentException("userAId and userBId must be different");
		}
		return new MatchProposal(userAId, userBId);
	}

	public void accept(Long userId) {
		validateCanDecide(userId);

		if (userId.equals(userAId)) {
			this.aDecision = MatchDecisionStatus.ACCEPTED;
		} else {
			this.bDecision = MatchDecisionStatus.ACCEPTED;
		}
		updateFinalStatusIfDecided();
	}

	public void reject(Long userId) {
		validateCanDecide(userId);

		if (userId.equals(userAId)) {
			this.aDecision = MatchDecisionStatus.REJECTED;
		} else {
			this.bDecision = MatchDecisionStatus.REJECTED;
		}
		this.status = MatchProposalStatus.REJECTED;
	}

	/**
	 * 공통 검증 로직: 결정 가능한 상태인지 확인
	 */
	private void validateCanDecide(Long userId) {
		// 1. 전체 매칭 상태 확인
		if (this.status == MatchProposalStatus.REJECTED) {
			throw new MatchProposalAlreadyRejectedException();
		}
		if (this.status == MatchProposalStatus.ACCEPTED) {
			throw new MatchProposalAlreadyProcessedException();
		}

		// 2. 권한 및 본인 중복 결정 확인
		if (userId.equals(userAId)) {
			if (this.aDecision != MatchDecisionStatus.PENDING) {
				throw new MatchProposalAlreadyDecidedException();
			}
		} else if (userId.equals(userBId)) {
			if (this.bDecision != MatchDecisionStatus.PENDING) {
				throw new MatchProposalAlreadyDecidedException();
			}
		} else {
			throw new MatchProposalNotParticipantException();
		}
	}

	public boolean isProposalAccepted() {
		return this.status == MatchProposalStatus.ACCEPTED;
	}

	private void updateFinalStatusIfDecided() {
		if (this.aDecision == MatchDecisionStatus.ACCEPTED && this.bDecision == MatchDecisionStatus.ACCEPTED) {
			this.status = MatchProposalStatus.ACCEPTED;
		}
	}
}
