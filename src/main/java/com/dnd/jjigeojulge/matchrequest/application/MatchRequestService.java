package com.dnd.jjigeojulge.matchrequest.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.global.common.dto.GeoPoint;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequest;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequestStatus;
import com.dnd.jjigeojulge.matchrequest.infra.MatchRequestRepository;
import com.dnd.jjigeojulge.matchrequest.presentation.data.MatchRequestDto;
import com.dnd.jjigeojulge.matchrequest.presentation.request.MatchRequestCreateRequest;
import com.dnd.jjigeojulge.user.domain.User;
import com.dnd.jjigeojulge.user.domain.exception.UserNotFoundException;
import com.dnd.jjigeojulge.user.infra.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchRequestService {

	private final MatchRequestRepository matchRequestRepository;
	private final UserRepository userRepository;

	@Transactional
	public MatchRequestDto create(Long userId, MatchRequestCreateRequest request) {
		// 1. 유저 엔티티 조회
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		// 2. 유저 당 1개 WAITING
		boolean exists = matchRequestRepository.existsByUserIdAndStatus(user.getId(), MatchRequestStatus.WAITING);

		// 3. 이미 요청이 waiting으로 존재할 경우 정책, 임시로 생성한 예외 이후 리팩토링
		// TODO 정확히 요청이 이미 존재할 경우 처리 방법 회의
		if (exists) {
			throw new IllegalStateException("이미 요청이 존재합니다.");
		}

		// 3. 생성 및 저장 (정적메 엔티티 내부에 생성)
		GeoPoint location = request.location();
		MatchRequest matchRequest = MatchRequest.builder()
			.latitude(BigDecimal.valueOf(location.latitude()))
			.longitude(BigDecimal.valueOf(request.location().longitude()))
			.specificPlace(request.specificPlace())
			.status(MatchRequestStatus.WAITING)
			.expectedDuration(request.expectedDuration())
			.requestMessage(request.requestMessage())
			.user(user)
			.build();

		MatchRequest saved = matchRequestRepository.save(matchRequest);
		return toDto(saved);
	}

	// preAuthorize 필요 + 어떻게 조회할 것인지? 유저 아이디 or match_request_id를 이용할 것인지?
	@Transactional(readOnly = true)
	public MatchRequestDto find(Long matchRequestId) {

		return null;
	}

	// preAuthorize 필요 + 어떻게 삭제할 것인가?
	@Transactional
	public void cancel() {

	}

	private static MatchRequestDto toDto(MatchRequest saved) {
		return MatchRequestDto.from(saved);
	}
}
