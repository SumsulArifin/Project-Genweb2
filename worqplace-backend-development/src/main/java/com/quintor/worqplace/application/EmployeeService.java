package com.quintor.worqplace.application;

import com.quintor.worqplace.application.exceptions.EmployeeNotFoundException;
import com.quintor.worqplace.data.EmployeeRepository;
import com.quintor.worqplace.domain.Employee;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service class that handles communication to the {@link EmployeeRepository repository}.
 *
 * @see Employee
 * @see EmployeeRepository
 */
@Service
@Transactional
@AllArgsConstructor
public class EmployeeService {
	private final EmployeeRepository employeeRepository;

	/**
	 * Function that gets the requested {@link Employee} by the entered id.
	 *
	 * @param id the id of the {@link Employee}.
	 * @return the {@link Employee} with the corresponding id.
	 * @throws EmployeeNotFoundException when no employee in the database matches
	 *                                   the entered id, this exception is thrown.
	 * @see Employee
	 * @see EmployeeNotFoundException
	 * @see ReservationService
	 */
	public Employee getEmployeeById(Long id) throws EmployeeNotFoundException {
		return employeeRepository
				.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException(id));
	}

	/**
	 * Function that creates an employee with the given parameters
	 * and then saves it.
	 *
	 * @param firstname first name of the employee
	 * @param lastname  last name of the employee
	 * @return the saved {@link Employee}
	 */
	public Employee saveEmployee(String firstname, String lastname) {
		return employeeRepository.save(new Employee(firstname, lastname));
	}
}
