package com.dnd.jjigeojulge.sse;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

	private final Map<Long, List<SseEmitter>> data = new ConcurrentHashMap<>();

	public SseEmitter save(Long receiverId, SseEmitter emitter) {
		Objects.requireNonNull(receiverId, "receiverId must not be null");
		Objects.requireNonNull(emitter, "emitter must not be null");

		data.computeIfAbsent(receiverId, id -> new CopyOnWriteArrayList<>())
			.add(emitter);

		return emitter;
	}

	public List<SseEmitter> findAll() {
		return data.values().stream()
			.flatMap(Collection::stream)
			.toList();
	}

	public Map<Long, List<SseEmitter>> findAllWithKeys() {
		return data;
	}

	public void delete(Long receiverId, SseEmitter emitter) {
		Objects.requireNonNull(receiverId, "receiverId must not be null");
		Objects.requireNonNull(emitter, "emitter must not be null");

		// key 단위로 "원자적으로" 수정 + 비면 key 제거
		data.computeIfPresent(receiverId, (id, list) -> {
			list.remove(emitter);
			return list.isEmpty() ? null : list; // null 반환 시 해당 key 제거(원자)
		});
	}
}
