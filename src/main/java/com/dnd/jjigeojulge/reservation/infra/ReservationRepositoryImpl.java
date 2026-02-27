package com.dnd.jjigeojulge.reservation.infra;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dnd.jjigeojulge.reservation.domain.Reservation;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

	private final JpaReservationRepository jpaReservationRepository;

	@Override
	public Reservation save(Reservation reservation) {
		return jpaReservationRepository.save(reservation);
	}

	@Override
	public Optional<Reservation> findById(Long id) {
		return jpaReservationRepository.findById(id);
	}

	@Override
	public void delete(Reservation reservation) {
		jpaReservationRepository.delete(reservation);
	}
}
