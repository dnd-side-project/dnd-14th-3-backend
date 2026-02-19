package com.dnd.jjigeojulge.matchproposal.infra;

import java.util.List;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Repository;

import com.dnd.jjigeojulge.global.common.dto.GeoPoint;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MatchGeoQueueRepository {

	private static final String KEY_GEO = "match:waiting:geo";

	private final StringRedisTemplate redisTemplate;

	public void addWaitingUser(Long userId, GeoPoint location) {
		redisTemplate.opsForGeo().add(
			KEY_GEO,
			new Point(location.longitude(), location.latitude()),
			userId.toString()
		);
	}

	public void removeWaitingUser(Long userId) {
		redisTemplate.opsForGeo().remove(KEY_GEO, userId.toString());
	}

	public List<Long> findNearBy(GeoPoint center, double radiusKm, int limit) {
		GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo()
			.search(
				KEY_GEO,
				GeoReference.fromCoordinate(new Point(center.longitude(), center.latitude())),
				new Distance(radiusKm, Metrics.KILOMETERS),
				RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs()
					.sortAscending()
					.limit(limit)
			);

		if (results == null) {
			return List.of();
		}

		return results.getContent().stream()
			.map(geoLocationGeoResult -> geoLocationGeoResult.getContent().getName())
			.map(Long::valueOf)
			.toList();
	}
}
