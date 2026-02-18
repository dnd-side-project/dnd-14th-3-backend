package com.dnd.jjigeojulge.sse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SseMessage {

	private String eventName;
	private Object eventData;
}
