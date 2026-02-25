package com.dnd.jjigeojulge.matchrequest.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.global.common.dto.GeoPoint;
import com.dnd.jjigeojulge.matchproposal.infra.MatchGeoQueueRepository;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequest;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequestStatus;
import com.dnd.jjigeojulge.matchrequest.exception.MatchRequestAlreadyProcessedException;
import com.dnd.jjigeojulge.matchrequest.exception.MatchRequestForbiddenException;
import com.dnd.jjigeojulge.matchrequest.exception.MatchRequestNotExpiredException;
import com.dnd.jjigeojulge.matchrequest.exception.MatchRequestNotFoundException;
import com.dnd.jjigeojulge.matchrequest.infra.MatchRequestRepository;
import com.dnd.jjigeojulge.matchrequest.presentation.data.MatchRequestDto;
import com.dnd.jjigeojulge.matchrequest.presentation.request.MatchRequestCreateRequest;
import com.dnd.jjigeojulge.user.domain.User;
import com.dnd.jjigeojulge.user.exception.UserNotFoundException;
import com.dnd.jjigeojulge.user.infra.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchRequestService {

	private static final int REQUEST_TTL_MIN = 5; // MVP: 5분 대기

	private final MatchRequestRepository matchRequestRepository;
	private final UserRepository userRepository;
	private final MatchGeoQueueRepository matchGeoQueueRepository;

	@Transactional
	public MatchRequestDto create(Long userId, MatchRequestCreateRequest request) {
		// 1. 유저 엔티티 조회
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		// 2. 유저 당 1개 WAITING
		boolean exists = matchRequestRepository.existsByUserIdAndStatus(user.getId(), MatchRequestStatus.WAITING);

		// 3. 이미 요청이 waiting으로 존재할 경우 정책
		if (exists) {
			throw new MatchRequestAlreadyProcessedException();
		}

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiresAt = now.plusMinutes(REQUEST_TTL_MIN);

		// 3. 생성 및 저장
		GeoPoint location = request.location();
		MatchRequest matchRequest = MatchRequest.builder()
			.latitude(BigDecimal.valueOf(location.latitude()))
			.longitude(BigDecimal.valueOf(location.longitude()))
			.specificPlace(request.specificPlace())
			.status(MatchRequestStatus.WAITING)
			.expectedDuration(request.expectedDuration())
			.requestMessage(request.requestMessage())
			.expiresAt(expiresAt)
			.user(user)
			.build();

		MatchRequest saved = matchRequestRepository.save(matchRequest);
		// TODO 트랜잭션 롤백 시 레디스도 롤백 되지 않는 부분 문제 해결 필요
		matchGeoQueueRepository.addWaitingUser(userId, location);
		return toDto(saved);
	}

	// preAuthorize 필요 + 어떻게 조회할 것인지? 유저 아이디 or match_request_id를 이용할 것인지?
	@Transactional(readOnly = true)
	public MatchRequestDto find(Long matchRequestId) {

		return null;
	}

	@Transactional
	public void cancel(Long userId) {
		matchRequestRepository.findByUserIdAndStatus(userId, MatchRequestStatus.WAITING)
			.ifPresent(matchRequest -> {
				matchRequest.cancel();
				matchGeoQueueRepository.removeWaitingUser(userId);
				log.info("MatchRequest cancelled. userId={}, matchRequestId={}", userId, matchRequest.getId());
			});
	}

	@Transactional
	public MatchRequestDto retry(Long userId, Long matchRequestId) {
		MatchRequest matchRequest = matchRequestRepository.findByIdWithUser(matchRequestId)
			.orElseThrow(MatchRequestNotFoundException::new);

		if (!matchRequest.isOwner(userId)) {
			throw new MatchRequestForbiddenException();
		}

		// 시간 기준으로 만료 여부 체크 (배치가 미처 status를 안 바꿨어도 통과)
		if (!matchRequest.isExpired(LocalDateTime.now())) {
			throw new MatchRequestNotExpiredException();
		}

		matchRequest.retry(LocalDateTime.now().plusMinutes(REQUEST_TTL_MIN));
		matchGeoQueueRepository.addWaitingUser(userId, matchRequest.toGeoPoint());

		return toDto(matchRequest);
	}

	private static MatchRequestDto toDto(MatchRequest saved) {
		return MatchRequestDto.from(saved);
	}
}
