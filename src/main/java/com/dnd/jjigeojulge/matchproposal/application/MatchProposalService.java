package com.dnd.jjigeojulge.matchproposal.application;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.event.MatchConfirmedEvent;
import com.dnd.jjigeojulge.event.MatchProposalAcceptedEvent;
import com.dnd.jjigeojulge.event.MatchProposalCreatedEvent;
import com.dnd.jjigeojulge.event.MatchProposalRejectedEvent;
import com.dnd.jjigeojulge.matchproposal.data.MatchProposalDto;
import com.dnd.jjigeojulge.matchproposal.domain.MatchProposal;
import com.dnd.jjigeojulge.matchproposal.domain.MatchProposalStatus;
import com.dnd.jjigeojulge.matchproposal.exception.MatchProposalNotFoundException;
import com.dnd.jjigeojulge.matchproposal.infra.MatchGeoQueueRepository;
import com.dnd.jjigeojulge.matchproposal.infra.MatchProposalRepository;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequest;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequestStatus;
import com.dnd.jjigeojulge.matchrequest.exception.MatchRequestNotFoundException;
import com.dnd.jjigeojulge.matchrequest.infra.MatchRequestRepository;
import com.dnd.jjigeojulge.matchsession.data.MatchSessionDto;
import com.dnd.jjigeojulge.matchsession.domain.MatchSession;
import com.dnd.jjigeojulge.matchsession.infra.MatchSessionRepository;
import com.dnd.jjigeojulge.user.domain.User;
import com.dnd.jjigeojulge.user.infra.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchProposalService {

	private final UserRepository userRepository;
	private final MatchProposalRepository matchProposalRepository;
	private final MatchSessionRepository matchSessionRepository;
	private final MatchRequestRepository matchRequestRepository;
	private final MatchGeoQueueRepository queueRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public MatchProposalDto createProposalAndDequeue(Long userAId, Long userBId) {
		MatchProposal proposal = MatchProposal.create(userAId, userBId);
		MatchProposal saved = matchProposalRepository.save(proposal);

		// MVP: 저장 직후 대기열 제거, 트랜잭션 롤백 시 redis 롤백은 안됨 이후에 리팩토링 필요
		queueRepository.removeWaitingUser(userAId);
		queueRepository.removeWaitingUser(userBId);

		MatchProposalDto dto = toDto(saved);
		eventPublisher.publishEvent(new MatchProposalCreatedEvent(dto));
		return dto;
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

	@Transactional
	public MatchProposalDto accept(Long userId, Long proposalId) {
		MatchProposal matchProposal = matchProposalRepository.findById(proposalId)
			.orElseThrow(MatchProposalNotFoundException::new);

		// TODO 고려 사항 : 동시에 두 유저가 상태를 변경할 경우, Lock 고려해야함.
		matchProposal.accept(userId);
		MatchProposalDto matchProposalDto = toDto(matchProposal);
		eventPublisher.publishEvent(new MatchProposalAcceptedEvent(userId, matchProposalDto));

		// 두 유저 모두 제안을 수락했을 경우
		if (matchProposal.isProposalAccepted()) {
			MatchRequest matchRequestA =
				matchRequestRepository.findByUserIdAndStatusFetchUser(matchProposal.getUserAId(),
						MatchRequestStatus.WAITING)
					.orElseThrow(MatchRequestNotFoundException::new);
			matchRequestA.match();
			User userA = matchRequestA.getUser();

			MatchRequest matchRequestB =
				matchRequestRepository.findByUserIdAndStatusFetchUser(matchProposal.getUserBId(),
						MatchRequestStatus.WAITING)
					.orElseThrow(MatchRequestNotFoundException::new);
			matchRequestB.match();
			User userB = matchRequestB.getUser();

			// MVP 세션 목적지는 제안 작성자(userA)의 위치로 설정
			// TODO 계산된 목적지 좌표 설정 기능
			MatchSession matchSession = MatchSession.create(
				userA, userB,
				matchRequestA,
				matchRequestB,
				matchRequestA.getLatitude(),
				matchRequestA.getLongitude()
			);
			MatchSession saved = matchSessionRepository.save(matchSession);
			eventPublisher.publishEvent(
				new MatchConfirmedEvent(new MatchSessionDto(saved.getId(), userA.getId(), userB.getId())));
		}

		return matchProposalDto;
	}

	@Transactional
	public MatchProposalDto reject(Long userId, Long proposalId) {
		MatchProposal matchProposal = matchProposalRepository.findById(proposalId)
			.orElseThrow(MatchProposalNotFoundException::new);
		matchProposal.reject(userId);
		MatchProposalDto matchProposalDto = toDto(matchProposal);

		eventPublisher.publishEvent(new MatchProposalRejectedEvent(userId, matchProposalDto));
		return matchProposalDto;
	}

	private MatchProposalDto toDto(MatchProposal saved) {
		return MatchProposalDto.from(saved);
	}
}
