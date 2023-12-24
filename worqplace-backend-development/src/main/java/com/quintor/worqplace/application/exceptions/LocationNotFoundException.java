package com.quintor.worqplace.application.exceptions;

/**
 * Exception that is thrown when the entered
 * {@link com.quintor.worqplace.domain.Location location} id
 * does not correspond to one in the database.
 *
 * @see com.quintor.worqplace.domain.Location Location
 */
public class LocationNotFoundException extends RuntimeException {
	public LocationNotFoundException(Long id) {
		super("Location " + id + " not found");
	}
}
