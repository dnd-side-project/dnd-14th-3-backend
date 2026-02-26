package com.dnd.jjigeojulge.matchproposal.application;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dnd.jjigeojulge.global.common.dto.GeoPoint;
import com.dnd.jjigeojulge.matchproposal.infra.MatchGeoQueueRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchProposalScheduler {

	private final MatchGeoQueueRepository queueRepository;
	private final MatchProposalService matchProposalService;

	// 튜닝 포인트 (MVP)
	private static final int BATCH_SIZE = 50;      // tick마다 처리할 대기자 수
	private static final int CANDIDATE_LIMIT = 20; // 근처 후보 최대 n명
	private static final double RADIUS_KM = 0.5;

	/*
	 * - 먼저 대기 풀에 들어온 사람 순서대로 탐색
	 * - 가장 가까운 사람 (가장 먼저 들어온 사람 기준) 이 먼저 찾아진다.
	 * */
	@Scheduled(fixedDelay = 1000)
	public void processWaitingMatchRequests() {
		List<Long> waitingUsers = queueRepository.scanWaitingUsers(BATCH_SIZE);
		if (waitingUsers.isEmpty()) {
			return;
		}

		for (Long userId : waitingUsers) {
			attemptMatchForUser(userId);
		}
	}

	private void attemptMatchForUser(Long userId) {
		GeoPoint myLocation = queueRepository.getLocation(userId).orElse(null);
		if (myLocation == null) {
			// waiting user repo에는 존재하는데, 대기 풀에서 나간 user 제거
			queueRepository.removeWaitingUser(userId);
			log.warn("Removed waiting user without GEO location. userId={}", userId);
			return;
		}

		List<Long> candidates = queueRepository.findNearBy(myLocation, RADIUS_KM, CANDIDATE_LIMIT)
			.stream()
			.filter(candidateId -> !candidateId.equals(userId)) // 자기 자신 제외
			.toList();

		if (candidates.isEmpty()) {
			// MVP: 후보 없으면 그냥 스킵
			return;
		}

		// 매칭 생성 전에 두 사람 간의 cancel 이 있었는지 검사한다. 조건은 오늘 범위 안에서
		// 만약 오늘 매칭이 cancel 된 적이 있다면, 다음 유저로 넘어간다.
		// 가장 가까운 후보 1명 선택 (sortAscending이라 첫 번째가 가장 가까움)
		for (Long otherId : candidates) {

			// 상대가 이미 빠졌을 수도 있으니 최소 확인(좌표 존재 여부)
			if (queueRepository.getLocation(otherId).isEmpty()) {
				continue;
			}

			// 오늘 취소 페어면 스킵하고 다음 후보 탐색
			if (matchProposalService.wasRejectedToday(userId, otherId)) {
				log.debug("Skip Rejected-today pair. a={} b={}", userId, otherId);
				continue;
			}

			// 페어 설정 플로우 진행
			matchProposalService.createProposalAndDequeue(userId, otherId);
			return;
		}
	}
}
