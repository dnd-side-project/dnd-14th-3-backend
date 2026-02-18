package com.dnd.jjigeojulge.sse;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

	private final Map<Long, List<SseEmitter>> data = new ConcurrentHashMap<>();

	public SseEmitter save(Long receiverId, SseEmitter sseEmitter) {
		data.putIfAbsent(receiverId, new CopyOnWriteArrayList<>());
		data.get(receiverId).add(sseEmitter);

		return sseEmitter;
	}

	public List<SseEmitter> findAll() {
		return data.values().stream()
			.flatMap(Collection::stream)
			.toList();
	}

	public Map<Long, List<SseEmitter>> findAllWithKeys() {
		return data;
	}

	public void delete(Long receiverId, SseEmitter sseEmitter) {
		if (data.containsKey(receiverId)) {
			List<SseEmitter> emitters = data.get(receiverId);
			emitters.remove(sseEmitter);

			if (emitters.isEmpty()) {
				data.remove(receiverId);
			}
		}
	}
}
