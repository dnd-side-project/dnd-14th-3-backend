package com.dnd.jjigeojulge.sse;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

	@Value("${sse.timeout}")
	private long timeout;

	private final SseEmitterRepository sseEmitterRepository;

	public SseEmitter connect(Long receiverId) {
		SseEmitter sseEmitter = new SseEmitter(timeout);

		sseEmitter.onCompletion(() -> {
			log.debug("sse on onCompletion");
			sseEmitterRepository.delete(receiverId, sseEmitter);
		});
		sseEmitter.onTimeout(() -> {
			log.debug("sse on onTimeout");
			sseEmitterRepository.delete(receiverId, sseEmitter);
		});
		sseEmitter.onError((ex) -> {
			log.debug("sse on onError");
			sseEmitterRepository.delete(receiverId, sseEmitter);
		});

		SseEmitter emitter = sseEmitterRepository.save(receiverId, sseEmitter);

		sendToEmitter(emitter, SseEmitter.event()
			.name("connect")
			.data("connected")
			.build());
		return emitter;
	}

	@Scheduled(fixedDelay = 30_000)
	public void clearUp() {
		Set<ResponseBodyEmitter.DataWithMediaType> ping = SseEmitter.event()
			.name("ping")
			.build();

		sseEmitterRepository.findAll().forEach(sseEmitter -> sendToEmitter(sseEmitter, ping));
	}

	public void send(SseMessage sseMessage) {
		Set<ResponseBodyEmitter.DataWithMediaType> event = sseMessage.toEvent();
		sseEmitterRepository.findAllByReceiverIdIn(sseMessage.getReceiverIds())
			.forEach(emitter -> sendToEmitter(emitter, event));
	}

	private void sendToEmitter(SseEmitter emitter, Set<ResponseBodyEmitter.DataWithMediaType> event) {
		try {
			emitter.send(event);
		} catch (Exception e) {
			log.error("SSE send failed", e);
			emitter.completeWithError(e);
		}
	}
}
