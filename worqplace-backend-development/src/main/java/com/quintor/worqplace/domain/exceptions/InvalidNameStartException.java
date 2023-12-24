package com.quintor.worqplace.domain.exceptions;

/**
 * Exception that is thrown if the location name is not valid
 *
 * @see com.quintor.worqplace.domain.Location#setName(String) name
 */
public class InvalidNameStartException extends RuntimeException {
	public InvalidNameStartException() {
		super("Name must start with a capital letter");
	}
}
