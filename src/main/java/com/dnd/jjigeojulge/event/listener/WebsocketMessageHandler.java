package com.dnd.jjigeojulge.event.listener;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.dnd.jjigeojulge.event.MatchSessionReadyEvent;
import com.dnd.jjigeojulge.event.MatchSessionUserArrivedEvent;
import com.dnd.jjigeojulge.matchsession.domain.MatchSessionStatus;
import com.dnd.jjigeojulge.websocket.data.ArrivalData;
import com.dnd.jjigeojulge.websocket.data.MatchSessionMessageDto;
import com.dnd.jjigeojulge.websocket.data.MatchSessionMessageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketMessageHandler {

	private final SimpMessagingTemplate messagingTemplate;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleMessage(MatchSessionReadyEvent event) {
		MatchSessionMessageDto<MatchSessionStatus> messageDto = MatchSessionMessageDto.of(
			MatchSessionMessageType.SESSION_READY,
			event.sessionId(),
			null,
			event.status()
		);
		String destination = String.format("/sub/sessions/%s/location", event.sessionId());
		messagingTemplate.convertAndSend(destination, messageDto);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleMessage(MatchSessionUserArrivedEvent event) {
		MatchSessionMessageDto<ArrivalData> messageDto = MatchSessionMessageDto.of(
			MatchSessionMessageType.USER_ARRIVED,
			event.sessionId(),
			event.userId(),
			new ArrivalData(true)
		);
		String destination = String.format("/sub/sessions/%s/location", event.sessionId());
		messagingTemplate.convertAndSend(destination, messageDto);
	}
}
