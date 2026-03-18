package com.dnd.jjigeojulge.sse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
	private final SseMessageRepository sseMessageRepository;

	public SseEmitter connect(Long receiverId, UUID lastEventId) {
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

		Set<ResponseBodyEmitter.DataWithMediaType> connect = SseEmitter.event()
			.name("connect")
			.data("connected")
			.build();
		try {
			sseEmitter.send(connect);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			sseEmitter.completeWithError(e);
		}

		Optional.ofNullable(lastEventId)
			.ifPresent(id -> {
					List<SseMessage> replayMessages =
						sseMessageRepository.findAllByLastEventIdAfterAndReceiverId(id, receiverId);
					log.info("SSE replay requested. receiverId={}, lastEventId={}, replayCount={}",
						receiverId, id, replayMessages.size());
					replayMessages.forEach(sseMessage -> sendToEmitter(emitter, sseMessage));
				}
			);
		return emitter;
	}

	public void send(SseMessage sseMessage) {
		sseMessageRepository.save(sseMessage);
		Set<ResponseBodyEmitter.DataWithMediaType> event = sseMessage.toEvent();
		sseEmitterRepository.findAllByReceiverIdIn(sseMessage.getReceiverIds())
			.forEach(emitter -> sendToEmitter(emitter, sseMessage));
	}

	private void sendToEmitter(SseEmitter emitter, SseMessage message) {
		try {
			emitter.send(message.toEvent());
			log.info("SSE send success. eventId={}, eventName={}", message.getEventId(), message.getEventName());
		} catch (IOException e) {
			log.error("SSE send failed. eventId={}, eventName={}, receiverId={}, message={}",
				message.getEventId(),
				message.getEventName(),
				message.getReceiverIds(),
				message.getEventData(),
				e);
			emitter.completeWithError(e);
		}
	}

	public Set<Long> getConnectedUserIds() {
		return sseEmitterRepository.getConnectedUserIds();
	}

	@Scheduled(fixedRate = 25_000)
	public void cleanUp() {
		Set<ResponseBodyEmitter.DataWithMediaType> ping = SseEmitter.event()
			.name("ping")
			.data("keep-alive")
			.build();
		sseEmitterRepository.findAll()
			.forEach(sseEmitter -> {
					try {
						sseEmitter.send(ping);
					} catch (IOException e) {
						log.error(e.getMessage(), e);
						sseEmitter.completeWithError(e);
					}
				}
			);
	}
}
