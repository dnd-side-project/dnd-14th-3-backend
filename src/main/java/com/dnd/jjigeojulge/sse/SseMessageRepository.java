package com.dnd.jjigeojulge.sse;

import java.util.List;
import java.util.UUID;

public interface SseMessageRepository {

	SseMessage save(SseMessage message);

	List<SseMessage> findAllByLastEventIdAfterAndReceiverId(UUID lastEventId, Long receiverId);
}
