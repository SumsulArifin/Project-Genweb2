package com.quintor.worqplace.application.exceptions;

/**
 * Exception that is thrown if the entered
 * {@link com.quintor.worqplace.domain.Room room} id
 * does not correspond to one in the database.
 *
 * @see com.quintor.worqplace.domain.Room Room
 */
public class RoomNotFoundException extends RuntimeException {
	public RoomNotFoundException(Long id) {
		super("Room " + id + " not found");
	}
}
