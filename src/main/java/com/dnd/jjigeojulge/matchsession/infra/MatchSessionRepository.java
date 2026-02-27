package com.dnd.jjigeojulge.matchsession.infra;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.jjigeojulge.matchsession.domain.MatchSession;

public interface MatchSessionRepository extends JpaRepository<MatchSession, Long> {

	@Query("select ms from MatchSession ms "
		+ "join fetch ms.userA "
		+ "join fetch ms.userB "
		+ "join fetch ms.userAMatchRequest "
		+ "join fetch ms.userBMatchRequest "
		+ "where ms.id = :id")
	Optional<MatchSession> findByIdWithAllDetails(@Param("id") Long id);
}
