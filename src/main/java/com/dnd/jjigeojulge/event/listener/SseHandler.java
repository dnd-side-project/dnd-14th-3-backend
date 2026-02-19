package com.dnd.jjigeojulge.event.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.dnd.jjigeojulge.event.MatchProposalCreatedEvent;
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
		
		SseMessage sseMessage = SseMessage.create(1L, "proposal", null);
		handleMessage(sseMessage);
	}

	private void handleMessage(SseMessage sseMessage) {
		sseService.send(sseMessage);
	}
}
