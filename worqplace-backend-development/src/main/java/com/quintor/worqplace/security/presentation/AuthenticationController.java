package com.quintor.worqplace.security.presentation;

import com.quintor.worqplace.domain.exceptions.InvalidNameException;
import com.quintor.worqplace.domain.exceptions.InvalidNameStartException;
import com.quintor.worqplace.security.application.UserService;
import com.quintor.worqplace.security.presentation.dto.Registration;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
@NoArgsConstructor
public class AuthenticationController {
	private UserService userService;

	/**
	 * Constructor for the class {@link AuthenticationController}.
	 *
	 * @param userService {@link UserService} will be injected by Spring Boot.
	 */
	@Autowired
	public AuthenticationController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Postmapping for registering a new user.
	 * Will validate the request and if the request is correct, create a user and the corresponding employee.
	 *
	 * @param registration {@link Registration} DTO
	 * @return {@link ResponseEntity} with info about how the request has been handled.
	 */
	@PostMapping
	public ResponseEntity<String> register(@RequestBody Registration registration) {
		try {
			if (validateRegistration(registration) != null)
				return validateRegistration(registration);

			var user = this.userService.register(
					registration.username(),
					registration.password(),
					registration.firstname(),
					registration.lastname()
			);

			return new ResponseEntity<>(
					"User with username \"%s\" has been created successfully".formatted(user.getUsername()),
					HttpStatus.CREATED
			);
		} catch (InvalidNameStartException | InvalidNameException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Method that validates the password length and username.
	 *
	 * @param registration {@link Registration} DTO.
	 * @return {@link ResponseEntity} if an invalid entry has been given, returns null if everything is correct.
	 */
	private ResponseEntity<String> validateRegistration(Registration registration) {
		if (registration.password().length() < 5)
			return new ResponseEntity<>(
					"InvalidPassword length %s, minimum is 5".formatted(5),
					HttpStatus.BAD_REQUEST
			);

		if (userService.findByUsername(registration.username()).isPresent())
			return new ResponseEntity<>(
					"User with username %s already exists".formatted(registration.username()),
					HttpStatus.BAD_REQUEST
			);

		return null;
	}
}
