package com.dnd.jjigeojulge.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails;
import com.dnd.jjigeojulge.global.common.dto.GeoPoint;
import com.dnd.jjigeojulge.websocket.data.MatchSessionMessageDto;
import com.dnd.jjigeojulge.websocket.data.MatchSessionMessageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LocationWebsocketController {

	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/sessions/{sessionId}/location")
	public void handleLocationUpdate(
		@DestinationVariable Long sessionId,
		@Payload GeoPoint payload,
		Authentication authentication
	) {
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();

		Long senderId = userDetails.id();
		log.info("메시지 보낸 유저 ID : {}", senderId);

		MatchSessionMessageDto<GeoPoint> responseDto = MatchSessionMessageDto.of(
			MatchSessionMessageType.LOCATION,
			sessionId,
			senderId,
			payload
		);
		String destination = String.format("/sub/sessions/%s/location", sessionId);
		messagingTemplate.convertAndSend(destination, responseDto);
	}
}
