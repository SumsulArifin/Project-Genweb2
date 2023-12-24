package com.quintor.worqplace.application.exceptions;

/**
 * Exception that is thrown if the selected amount of workplaces
 * is not available at the selected timeslot.
 *
 * @see com.quintor.worqplace.domain.Room Room
 * @see com.quintor.worqplace.domain.Reservation Reservation
 * @see com.quintor.worqplace.application.ReservationService ReservationService
 */
public class WorkplacesNotAvailableException extends RuntimeException {
	public WorkplacesNotAvailableException(int wanted, int available) {
		super("Requested " + wanted + " workplaces but only " + available + " available.");
	}
}
