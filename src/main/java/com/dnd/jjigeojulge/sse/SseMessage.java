package com.dnd.jjigeojulge.sse;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SseMessage {

	private UUID eventId;
	private String eventName;
	private Object eventData;
	private Set<Long> receiverIds = new HashSet<>();

	public static SseMessage create(Long receiverId, String eventName, Object eventData) {
		return new SseMessage(
			UUID.randomUUID(),
			eventName,
			eventData,
			Set.of(receiverId)
		);
	}

	public static SseMessage create(Collection<Long> receiverIds, String eventName, Object eventData) {
		return new SseMessage(
			UUID.randomUUID(),
			eventName,
			eventData,
			new HashSet<>(receiverIds)
		);
	}

	public Set<ResponseBodyEmitter.DataWithMediaType> toEvent() {
		return SseEmitter.event()
			.id(eventId.toString())
			.name(eventName)
			.data(eventData)
			.build();
	}
}
