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
		validateCanAccept(userId);

		if (userId.equals(userAId)) {
			this.aDecision = MatchDecisionStatus.ACCEPTED;
		} else {
			this.bDecision = MatchDecisionStatus.ACCEPTED;
		}
		updateFinalStatusIfDecided();
	}

	public void reject(Long userId) {
		validateCanReject(userId);

		if (userId.equals(userAId)) {
			this.aDecision = MatchDecisionStatus.REJECTED;
		} else {
			this.bDecision = MatchDecisionStatus.REJECTED;
		}
		this.status = MatchProposalStatus.REJECTED;
	}

	/**
	 * 수락 가능 검증:
	 * - 제안이 PENDING이어야 함
	 * - 참여자여야 함
	 * - 본인은 아직 결정을 하지 않았어야 함 (PENDING만 가능)
	 */
	private void validateCanAccept(Long userId) {
		validateCommonStatus(userId);

		MatchDecisionStatus myDecision = getMyDecision(userId);
		if (myDecision != MatchDecisionStatus.PENDING) {
			// 이미 ACCEPTED/REJECTED 했으면 수락 재시도 불가
			throw new MatchProposalAlreadyDecidedException();
		}
	}

	/**
	 * 거절 가능 검증:
	 * - 제안이 PENDING이어야 함
	 * - 참여자여야 함
	 * - 본인이 이미 REJECTED면 중복 거절 불가
	 * - 본인이 ACCEPTED였던 경우는 "대기 중 마음 변경"이므로 거절 허용
	 */
	private void validateCanReject(Long userId) {
		validateCommonStatus(userId);

		MatchDecisionStatus myDecision = getMyDecision(userId);
		if (myDecision == MatchDecisionStatus.REJECTED) {
			throw new MatchProposalAlreadyDecidedException();
		}
	}

	private MatchDecisionStatus getMyDecision(Long userId) {
		if (userId.equals(userAId)) {
			return this.aDecision;
		}
		if (userId.equals(userBId)) {
			return this.bDecision;
		}

		throw new MatchProposalNotParticipantException();
	}

	private void validateCommonStatus(Long userId) {
		// 1) 전체 상태 확인
		if (this.status == MatchProposalStatus.REJECTED) {
			throw new MatchProposalAlreadyRejectedException();
		}
		if (this.status == MatchProposalStatus.ACCEPTED) {
			// 최종 확정(양쪽 수락) 이후는 변경 불가
			throw new MatchProposalAlreadyProcessedException();
		}

		// 2) 참여자 확인
		if (!userId.equals(userAId) && !userId.equals(userBId)) {
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
