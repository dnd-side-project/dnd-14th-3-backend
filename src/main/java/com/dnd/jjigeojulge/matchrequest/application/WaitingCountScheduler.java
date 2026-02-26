package com.dnd.jjigeojulge.matchrequest.application;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dnd.jjigeojulge.global.common.dto.GeoPoint;
import com.dnd.jjigeojulge.matchproposal.infra.MatchGeoQueueRepository;
import com.dnd.jjigeojulge.matchrequest.presentation.data.WaitingCountChangedDto;
import com.dnd.jjigeojulge.sse.SseMessage;
import com.dnd.jjigeojulge.sse.SseService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WaitingCountScheduler {

	private final MatchGeoQueueRepository queueRepository;
	private final SseService sseService; // emit(userId, eventName, data)
	private final ConcurrentHashMap<Long, Integer> last = new ConcurrentHashMap<>();

	private static final double RADIUS_KM = 0.5;
	private static final int COUNT_LIMIT = 200;

	@Scheduled(fixedDelay = 1500)
	public void pushWaitingCounts() {
		Set<Long> connectedUserIds = sseService.getConnectedUserIds();
		last.keySet().removeIf(id -> !connectedUserIds.contains(id));
		
		for (Long userId : connectedUserIds) {
			GeoPoint geoPoint = queueRepository.getLocation(userId).orElse(null);
			if (geoPoint == null) {
				last.remove(userId);
				continue;
			}

			int nearByCount = Math.max(0, queueRepository.countNearByExcludeMe(geoPoint, RADIUS_KM, COUNT_LIMIT));

			Integer prev = last.put(userId, nearByCount);

			if (!Objects.equals(prev, nearByCount)) {
				SseMessage sseMessage = SseMessage.create(
					userId,
					"match.request.waiting-count",
					new WaitingCountChangedDto(nearByCount)
				);
				sseService.send(sseMessage);
			}
		}
	}
}
