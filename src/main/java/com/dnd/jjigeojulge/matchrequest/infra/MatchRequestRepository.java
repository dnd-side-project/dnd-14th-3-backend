package com.dnd.jjigeojulge.matchrequest.infra;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.jjigeojulge.matchrequest.domain.MatchRequest;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequestStatus;

public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

	@Query("""
			select mr.user.id
			from MatchRequest mr
			where mr.status = :status and mr.expiresAt <= :now
			order by mr.expiresAt asc
		""")
	List<Long> findExpiredWaitingUserIds(
		@Param("status") MatchRequestStatus status,
		@Param("now") LocalDateTime now,
		Pageable pageable
	);

	boolean existsByUserIdAndStatus(Long userId, MatchRequestStatus status);

	Optional<MatchRequest> findByUserIdAndStatus(Long userId, MatchRequestStatus status);
}
