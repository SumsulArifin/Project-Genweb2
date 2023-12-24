package com.quintor.worqplace.domain.exceptions;

/**
 * Exception that is thrown when the given street doesn't comply with the set boundaries for street naming
 *
 * @see com.quintor.worqplace.domain.Address#setStreet(String) street
 */
public class InvalidStreetException extends RuntimeException {
	public InvalidStreetException(String specification) {
		super(specification);
	}
}
