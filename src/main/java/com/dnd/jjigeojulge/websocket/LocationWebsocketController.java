package com.dnd.jjigeojulge.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails;
import com.dnd.jjigeojulge.global.common.dto.GeoPoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LocationWebsocketController {

	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/sessions/{sessionId}/location")
	public void handleLocationUpdate(
		@DestinationVariable String sessionId,
		@Payload GeoPoint payload,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = userDetails.id();
		LocationDto locationDto = LocationDto.of(userId, payload.latitude(), payload.longitude());
		String destination = String.format("/sub/sessions/%s/location", sessionId);
		messagingTemplate.convertAndSend(destination, locationDto);
	}
}
