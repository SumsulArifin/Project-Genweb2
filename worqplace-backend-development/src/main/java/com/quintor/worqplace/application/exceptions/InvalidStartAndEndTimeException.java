package com.quintor.worqplace.application.exceptions;

/**
 * Exception that is thrown when the entered end time is before the entered start time.
 *
 * @see com.quintor.worqplace.application.util.DateTimeUtils DateTimeUtils
 * @see java.time.LocalTime
 */
public class InvalidStartAndEndTimeException extends RuntimeException {
	public InvalidStartAndEndTimeException() {
		super("The reservation start time cannot be after the end time!");
	}
}
