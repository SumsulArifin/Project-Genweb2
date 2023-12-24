package com.quintor.worqplace.domain.exceptions;

/**
 * Exception that is thrown if the name contains invalid characters
 *
 * @see com.quintor.worqplace.domain.Location#setName(String) location name
 * @see com.quintor.worqplace.domain.Employee employee
 */
public class InvalidNameException extends RuntimeException {
	public InvalidNameException(char c) {
		super(String.format("Name contains invalid character: \"%c\"", c));
	}
}
