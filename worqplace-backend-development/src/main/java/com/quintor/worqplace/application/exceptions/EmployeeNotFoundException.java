package com.quintor.worqplace.application.exceptions;

/**
 * Exception that is thrown when actions are taken under an
 * {@link com.quintor.worqplace.domain.Employee employee} id that does not
 * correspond to one in the database.
 *
 * @see com.quintor.worqplace.domain.Employee Employee
 */
public class EmployeeNotFoundException extends RuntimeException {
	public EmployeeNotFoundException(Long id) {
		super("Employee " + id + " not found");
	}
}
