package com.dnd.jjigeojulge.matchrequest.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.event.MatchRequestExpiredEvent;
import com.dnd.jjigeojulge.matchproposal.infra.MatchGeoQueueRepository;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequestStatus;
import com.dnd.jjigeojulge.matchrequest.infra.MatchRequestRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchRequestExpiryScheduler {

	private final MatchRequestRepository matchRequestRepository;
	private final MatchGeoQueueRepository matchGeoQueueRepository;
	private final ApplicationEventPublisher eventPublisher;

	private static final int EXPIRE_BATCH_SIZE = 200;

	// TODO 디비 조회 줄이기: 레디스 만료 키 이용
	@Scheduled(fixedDelay = 30_000)
	@Transactional
	public void expireOverdue() {
		LocalDateTime now = LocalDateTime.now();

		List<Long> expiredUserIds = matchRequestRepository.findExpiredWaitingUserIds(
			MatchRequestStatus.WAITING,
			now,
			PageRequest.of(0, EXPIRE_BATCH_SIZE)
		);

		if (expiredUserIds.isEmpty()) {
			return;
		}

		for (Long userId : expiredUserIds) {
			matchRequestRepository.findByUserIdAndStatus(userId, MatchRequestStatus.WAITING)
				.ifPresent(matchRequest -> {
					// 상태 전이
					matchRequest.expire();

					// Redis 정리
					matchGeoQueueRepository.removeWaitingUser(userId);

					// SSE 발행(이벤트로)
					eventPublisher.publishEvent(
						new MatchRequestExpiredEvent(userId, matchRequest.getId(), matchRequest.getExpiresAt()));

					log.info("MatchRequest expired. userId={}, matchRequestId={}", userId, matchRequest.getId());
				});
		}
	}
}
