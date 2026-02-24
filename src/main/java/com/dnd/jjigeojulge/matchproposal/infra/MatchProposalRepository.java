package com.dnd.jjigeojulge.matchproposal.infra;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.jjigeojulge.matchproposal.domain.MatchProposal;
import com.dnd.jjigeojulge.matchproposal.domain.MatchProposalStatus;

public interface MatchProposalRepository extends JpaRepository<MatchProposal, Long> {

	@Query("""
			select (count(mp) > 0)
			from MatchProposal mp
			where mp.status = :status
			  and mp.createdAt >= :from
			  and mp.createdAt < :to
			  and (
			       (mp.userAId = :a and mp.userBId = :b)
			    or (mp.userAId = :b and mp.userBId = :a)
			  )
		""")
	boolean existsPairWithStatusInRange(
		@Param("a") Long a,
		@Param("b") Long b,
		@Param("status") MatchProposalStatus status,
		@Param("from") LocalDateTime from,
		@Param("to") LocalDateTime to
	);
}
