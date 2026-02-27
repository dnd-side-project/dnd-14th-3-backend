package com.dnd.jjigeojulge.reservation.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.jjigeojulge.reservation.domain.Reservation;

public interface JpaReservationRepository extends JpaRepository<Reservation, Long> {
}
