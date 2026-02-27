package com.dnd.jjigeojulge.reservation.domain.repository;

import java.util.Optional;

import com.dnd.jjigeojulge.reservation.domain.Reservation;

public interface ReservationRepository {
	Reservation save(Reservation reservation);

	Optional<Reservation> findById(Long id);

	void delete(Reservation reservation);
}
