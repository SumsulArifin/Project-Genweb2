package com.quintor.worqplace.data;

import com.quintor.worqplace.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class that connects the {@link Employee}
 * application to the database to store the domain.
 *
 * @see Employee
 * @see com.quintor.worqplace.application.EmployeeService EmployeeService
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
