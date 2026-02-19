package com.dnd.jjigeojulge.matchproposal.infra;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
	private static final String KEY_WAITING_USERS = "match:waiting:users";

	private final StringRedisTemplate redisTemplate;

	public void addWaitingUser(Long userId, GeoPoint location) {
		String member = userId.toString();
		redisTemplate.opsForGeo().add(
			KEY_GEO,
			new Point(location.longitude(), location.latitude()),
			member
		);

		redisTemplate.opsForSet().add(KEY_WAITING_USERS, member);
	}

	public void removeWaitingUser(Long userId) {
		String member = userId.toString();

		redisTemplate.opsForGeo().remove(KEY_GEO, member);
		redisTemplate.opsForSet().remove(KEY_WAITING_USERS, member);
	}

	public List<Long> scanWaitingUsers(int limit) {
		Set<String> members = redisTemplate.opsForSet().members(KEY_WAITING_USERS);
		if (members == null || members.isEmpty()) {
			return List.of();
		}
		return members.stream()
			.limit(limit)
			.map(Long::valueOf)
			.toList();
	}

	/** (스케줄러용) 특정 유저의 현재 좌표 조회 (GEOPOS) */
	public Optional<GeoPoint> getLocation(Long userId) {
		List<Point> positions = redisTemplate.opsForGeo().position(KEY_GEO, userId.toString());
		if (positions == null || positions.isEmpty() || positions.get(0) == null) {
			return Optional.empty();
		}
		Point p = positions.get(0);
		// Point.getX()=lon, getY()=lat
		return Optional.of(new GeoPoint(p.getX(), p.getY()));
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
