package com.dnd.jjigeojulge.sse;

import java.io.IOException;
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

		return sseEmitterRepository.save(receiverId, sseEmitter);
	}

	@Scheduled(cron = "0 */30 * * * *")
	public void heartbeat() {
		Set<ResponseBodyEmitter.DataWithMediaType> ping = SseEmitter.event()
			.name("ping")
			.build();

		sseEmitterRepository.findAllWithKeys().forEach((receiverId, sseEmitters) -> {
			for (SseEmitter sseEmitter : sseEmitters) {
				try {
					sseEmitter.send(ping);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					sseEmitter.completeWithError(e);
					sseEmitterRepository.delete(receiverId, sseEmitter);
				}
			}
		});
	}

	public void send(SseMessage sseMessage) {

	}
}
