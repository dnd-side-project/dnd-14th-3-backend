package com.dnd.jjigeojulge.matchrequest.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.jjigeojulge.matchrequest.domain.MatchRequest;
import com.dnd.jjigeojulge.matchrequest.domain.MatchRequestStatus;

public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {
	boolean existsByUserIdAndStatus(Long userId, MatchRequestStatus status);
}
