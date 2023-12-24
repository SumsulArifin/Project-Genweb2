package com.quintor.worqplace.domain;

import com.quintor.worqplace.domain.exceptions.InvalidNameException;
import com.quintor.worqplace.domain.exceptions.InvalidNameStartException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeTest {
	private String firstName;
	private String lastName;

	@BeforeEach
	void initialize() {
		this.firstName = "TestFirstName";
		this.lastName = "TestLastName";
	}

	@Test
	@DisplayName("Should create employee correctly")
	void shouldNotThrowWhenCorrectEmployee() {
		assertDoesNotThrow(() -> new Employee(firstName, lastName));
	}

	@Test
	@DisplayName("Should throw InvalidNameStartException if first name doesn't start with a capital letter")
	void shouldThrowWhenFirstNameDoesNotStartWithCapitalLetter() {
		assertThrows(InvalidNameStartException.class, () -> new Employee("testFirstName", lastName));
	}

	@Test
	@DisplayName("Should throw InvalidNameStartException if last name doesn't start with a capital letter")
	void shouldThrowWhenLastNameDoesNotStartWithCapitalLetter() {
		assertThrows(InvalidNameStartException.class, () -> new Employee(firstName, "testLastName"));
	}

	@Test
	@DisplayName("Should throw InvalidNameException if first name contains digits")
	void shouldThrowWhenFirstNameContainsDigits() {
		assertThrows(InvalidNameException.class, () -> new Employee("Test3firstName", lastName));
	}

	@Test
	@DisplayName("Should throw InvalidNameException if last name contains digits")
	void shouldThrowWhenLastNameContainsDigits() {
		assertThrows(InvalidNameException.class, () -> new Employee(firstName, "L0stName"));
	}

	@Test
	@DisplayName("Should throw InvalidNameException if first name contains symbols")
	void shouldThrowWhenFirstNameContainsSymbols() {
		assertThrows(InvalidNameException.class, () -> new Employee("FirstN@me", lastName));
	}

	@Test
	@DisplayName("Should throw InvalidNameException if last name contains symbols")
	void shouldThrowWhenLastNameContainsSymbols() {
		assertThrows(InvalidNameException.class, () -> new Employee(firstName, "L^astName"));
	}
}
