package com.quintor.worqplace.application.exceptions;

/**
 * Exception that is thrown when an entered day is before the current day.
 *
 * @see com.quintor.worqplace.application.util.DateTimeUtils DateTimeUtils
 * @see java.time.LocalDate LocalDate
 */
public class InvalidDayException extends RuntimeException {
	public InvalidDayException() {
		super("The reservation day could not be before today!");
	}
}
