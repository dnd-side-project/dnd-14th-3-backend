package com.dnd.jjigeojulge.matchsession.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.global.common.dto.GeoPoint;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequest;
import com.dnd.jjigeojulge.matchsession.domain.MatchSession;
import com.dnd.jjigeojulge.matchsession.exception.MatchSessionNotFoundException;
import com.dnd.jjigeojulge.matchsession.exception.MatchSessionNotParticipantException;
import com.dnd.jjigeojulge.matchsession.infra.MatchSessionRepository;
import com.dnd.jjigeojulge.matchsession.presentation.data.SessionDto;
import com.dnd.jjigeojulge.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchSessionService {

	private final MatchSessionRepository matchSessionRepository;

	@Transactional(readOnly = true)
	public SessionDto getSessionDetail(Long currentUserId, Long sessionId) {
		MatchSession matchSession = matchSessionRepository.findByIdWithAllDetails(sessionId)
			.orElseThrow(MatchSessionNotFoundException::new);

		if (!matchSession.isParticipant(currentUserId)) {
			throw new MatchSessionNotParticipantException();
		}
		return toDto(matchSession, currentUserId);
	}

	private SessionDto toDto(MatchSession matchSession, Long currentUserId) {
		boolean isUserA = matchSession.getUserA().getId().equals(currentUserId);

		// 내 정보와 상대방 정보 분리 추출
		User userMe = isUserA ? matchSession.getUserA() : matchSession.getUserB();
		User userPartner = isUserA ? matchSession.getUserB() : matchSession.getUserA();

		boolean arrivedMe = isUserA ? matchSession.isArrivedA() : matchSession.isArrivedB();
		boolean arrivedPartner = isUserA ? matchSession.isArrivedB() : matchSession.isArrivedA();

		MatchRequest requestMe = isUserA ? matchSession.getUserAMatchRequest() : matchSession.getUserBMatchRequest();
		MatchRequest requestPartner =
			isUserA ? matchSession.getUserBMatchRequest() : matchSession.getUserAMatchRequest();

		return new SessionDto(
			matchSession.getId(),
			matchSession.getStatus(),
			new GeoPoint(matchSession.getDestLatitude().doubleValue(), matchSession.getDestLongitude().doubleValue()),
			matchSession.getMatchedAt(),
			matchSession.getEndedAt(),
			createUserSummaryDto(userMe, arrivedMe, requestMe),
			createUserSummaryDto(userPartner, arrivedPartner, requestPartner)
		);
	}

	private SessionDto.UserSummaryDto createUserSummaryDto(User user, boolean arrived, MatchRequest request) {
		return new SessionDto.UserSummaryDto(
			user.getId(),
			user.getNickname(),
			user.getGender(),
			arrived,
			new SessionDto.MatchRequestSummaryDto(
				request.getId(),
				request.getStatus(),
				request.getSpecificPlace(),
				request.getRequestMessage(),
				request.getExpectedDuration()
			)
		);
	}
}
