package com.dnd.jjigeojulge.event.listener;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.dnd.jjigeojulge.event.MatchProposalCreatedEvent;
import com.dnd.jjigeojulge.matchproposal.data.MatchProposalDto;
import com.dnd.jjigeojulge.matchsession.data.MatchSessionDto;
import com.dnd.jjigeojulge.sse.SseMessage;
import com.dnd.jjigeojulge.sse.SseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseHandler {

	private final SseService sseService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(MatchProposalCreatedEvent event) {
		MatchProposalDto matchProposalDto = event.matchProposalDto();
		Long userAId = matchProposalDto.userAId();
		Long userBId = matchProposalDto.userBId();
		if (userAId == null || userBId == null) {
			log.warn("SSE 전송 실패: matchSession의 userAId 또는 userBId가 null입니다.");
			return;
		}
		Set<Long> receiverIds = Set.of(userAId, userBId);
		SseMessage sseMessage = SseMessage.create(receiverIds, "match.proposal", matchProposalDto);
		handleMessage(sseMessage);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(MatchConfirmedEvent event) {
		MatchSessionDto dto = event.matchSessionDto();
		Set<Long> receiverIds = Set.of(dto.userAId(), dto.userBId());
		SseMessage sseMessage = SseMessage.create(receiverIds, "match.session", event.matchSessionDto());
		handleMessage(sseMessage);
	}

	private void handleMessage(SseMessage sseMessage) {
		sseService.send(sseMessage);
	}
}
