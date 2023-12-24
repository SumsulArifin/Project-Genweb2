package com.quintor.worqplace.application.exceptions;

/**
 * Exception that is thrown if the entered
 * {@link com.quintor.worqplace.domain.Reservation reservation} id
 * does not correspond to one in the database.
 *
 * @see com.quintor.worqplace.domain.Reservation Reservation
 */
public class ReservationNotFoundException extends RuntimeException {
	public ReservationNotFoundException(Long id) {
		super("Reservation " + id + " not found");
	}
}
