package com.quintor.worqplace.domain;

import com.quintor.worqplace.domain.exceptions.InvalidNameException;
import com.quintor.worqplace.domain.exceptions.InvalidNameStartException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Class containing {@link Employee} data like first name
 * and last name.
 *
 * @see Reservation
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee")
public class Employee implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String firstName;
	private String lastName;

	/**
	 * Constructor of the {@link Employee} class. This constructor calls
	 * the class' setter methods instead of setting the variables directly,
	 * this is because the setters contain logic to verify input data.
	 *
	 * @param firstName the employee's first name.
	 * @param lastName  the employee's last name.
	 */
	public Employee(String firstName, String lastName) {
		setFirstName(firstName);
		setLastName(lastName);
	}

	/**
	 * Function that updates the first name of the {@link Employee}.
	 * It also checks if the name consists of letters only and starts with a capital
	 * letter and throws an {@link InvalidNameStartException invalidNameStartException}
	 * or {@link InvalidNameException invalidNameException} if not.
	 *
	 * @param firstName the to-be set first name.
	 * @throws InvalidNameStartException when the input name does not start with a capital letter,
	 *                                   it throws this exception.
	 * @throws InvalidNameException      when the input name does not consist of letters only,
	 *                                   it throws this exception.
	 */
	public void setFirstName(String firstName) {
		char[] firstNameChars = firstName.toCharArray();

		if (!Character.isUpperCase(firstNameChars[0]))
			throw new InvalidNameStartException();

		for (char c : firstNameChars)
			if (!Character.isLetter(c))
				throw new InvalidNameException(c);


		this.firstName = firstName;
	}

	/**
	 * Function that updates the last name of the {@link Employee}.
	 * It also checks if the name consists of letters only and starts with a capital
	 * letter and throws an {@link InvalidNameStartException invalidNameStartException}
	 * or {@link InvalidNameException invalidNameException} if not.
	 *
	 * @param lastName the to-be set first name.
	 * @throws InvalidNameStartException when the input name does not start with a capital letter,
	 *                                   it throws this exception.
	 * @throws InvalidNameException      when the input name does not consist of letters only,
	 *                                   it throws this exception.
	 */
	public void setLastName(String lastName) {
		char[] lastNameChars = lastName.toCharArray();

		if (!Character.isUpperCase(lastNameChars[0]))
			throw new InvalidNameStartException();

		for (char c : lastNameChars)
			if (!Character.isLetter(c))
				throw new InvalidNameException(c);

		this.lastName = lastName;
	}
}
