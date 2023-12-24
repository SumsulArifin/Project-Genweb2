package com.quintor.worqplace.security.application;

import com.quintor.worqplace.application.EmployeeService;
import com.quintor.worqplace.domain.Employee;
import com.quintor.worqplace.security.data.SpringUserRepository;
import com.quintor.worqplace.security.data.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Implements UserDetailsService in order to make it usable
 * as login/registration service for Spring.
 * (see AuthenticationFilter)
 */
@Service
@Transactional
@AllArgsConstructor
public class UserService implements UserDetailsService {
	private final SpringUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private EmployeeService employeeService;

	/**
	 * Function that registers a new user.
	 * It will encode the password, creates and saves a corresponding {@link Employee Employee} object.
	 * Then saves the user.
	 *
	 * @param username  Email address of the user.
	 * @param password  Password of the user.
	 * @param firstname Firstname of the user.
	 * @param lastname  Lastname of the user.
	 * @return User object if save is succesful.
	 */
	public User register(String username, String password, String firstname, String lastname) {
		String encodedPassword = this.passwordEncoder.encode(password);

		Employee employee = employeeService.saveEmployee(firstname, lastname);
		if (employee == null)
			return null;

		var user = new User(employee.getId(), username, encodedPassword, employee);
		return this.userRepository.save(user);
	}

	/**
	 * Function that loads the user if the user with the given username
	 * is present in the database.
	 *
	 * @param username In our case this will be the email address of the user.
	 * @return User object if found.
	 * @throws UsernameNotFoundException When a user has not been found.
	 */
	@Override
	public User loadUserByUsername(String username) {
		return this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));
	}

	/**
	 * Funtion to search the database for a user with the given username.
	 *
	 * @param username Username.
	 * @return {@link Optional} of User.
	 */
	public Optional<User> findByUsername(String username) {
		return this.userRepository.findByUsername(username);
	}
}
