package com.quintor.worqplace.domain.exceptions;

/**
 * Exception that is thrown when the given city doesn't comply with the set boundaries for city naming
 *
 * @see com.quintor.worqplace.domain.Address#setCity(String) city
 */
public class InvalidCityException extends RuntimeException {
	public InvalidCityException() {
		super("City name must consist of only letters");
	}
}
