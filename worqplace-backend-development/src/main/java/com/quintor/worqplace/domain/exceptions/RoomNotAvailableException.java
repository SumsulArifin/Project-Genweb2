package com.quintor.worqplace.domain.exceptions;

/**
 * Exception that is thrown if the selected
 * {@link com.quintor.worqplace.domain.Room room} is not available
 * at the selected timeslot.
 *
 * @see com.quintor.worqplace.domain.Room Room
 * @see com.quintor.worqplace.domain.Reservation Reservation
 */
public class RoomNotAvailableException extends RuntimeException {
	public RoomNotAvailableException() {
		super("Room is not available");
	}
}
