package com.dnd.jjigeojulge.matchproposal.application;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.matchproposal.data.MatchProposalDto;
import com.dnd.jjigeojulge.matchproposal.domain.MatchProposal;
import com.dnd.jjigeojulge.matchproposal.domain.MatchProposalStatus;
import com.dnd.jjigeojulge.matchproposal.infra.MatchGeoQueueRepository;
import com.dnd.jjigeojulge.matchproposal.infra.MatchProposalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchProposalService {

	private final MatchProposalRepository matchProposalRepository;
	private final MatchGeoQueueRepository queueRepository;

	@Transactional
	public MatchProposalDto createProposalAndDequeue(Long userAId, Long userBId) {
		MatchProposal proposal = MatchProposal.create(userAId, userBId);
		MatchProposal saved = matchProposalRepository.save(proposal);

		// MVP: 저장 직후 대기열 제거, 트랜잭션 롤백 시 redis 롤백은 안됨 이후에 리팩토링 필요
		queueRepository.removeWaitingUser(userAId);
		queueRepository.removeWaitingUser(userBId);

		return toDto(saved);
	}

	@Transactional(readOnly = true)
	public boolean wasRejectedToday(Long a, Long b) {
		LocalDate today = LocalDate.now(); // 서버 timezone KST로 맞춰져 있어야 함
		LocalDateTime from = today.atStartOfDay();
		LocalDateTime to = today.plusDays(1).atStartOfDay();

		return matchProposalRepository.existsPairWithStatusInRange(
			a, b, MatchProposalStatus.REJECTED, from, to
		);
	}

	private MatchProposalDto toDto(MatchProposal saved) {
		return MatchProposalDto.from(saved);
	}
}
