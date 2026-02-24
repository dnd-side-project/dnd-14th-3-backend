package com.dnd.jjigeojulge.matchsession.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.jjigeojulge.matchsession.domain.MatchSession;

public interface MatchSessionRepository extends JpaRepository<MatchSession, Long> {
}
