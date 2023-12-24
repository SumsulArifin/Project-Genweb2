package com.quintor.worqplace.domain.exceptions;

/**
 * Exception that is thrown when the given postal code doesn't comply with the set boundaries for postal codes
 *
 * @see com.quintor.worqplace.domain.Address#setPostalCode(String) postalCode
 */
public class InvalidPostalCodeException extends RuntimeException {
	public InvalidPostalCodeException(String cause) {
		super(cause);
	}
}
