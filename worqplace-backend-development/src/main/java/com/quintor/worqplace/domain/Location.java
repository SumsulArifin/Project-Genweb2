package com.quintor.worqplace.domain;

import com.quintor.worqplace.domain.exceptions.InvalidNameStartException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Class containing {@link Location} data like the name, {@link Address} and what
 * {@link Room rooms} are at this location.
 *
 * @see Address
 * @see Room
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "location")
public class Location {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@OneToOne
	private Address address;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "location_id")
	private List<Room> rooms;

	/**
	 * Constructor of the {@link Location} class. This constructor calls
	 * the class' setter method of the name instead of setting the variables directly,
	 * this is because the setter contains logic to verify input data.
	 *
	 * @param name    name of the location.
	 * @param address {@link Address address} of the location.
	 * @param rooms   {@link Room rooms} in this location.
	 */
	public Location(String name, Address address, List<Room> rooms) {
		setName(name);
		this.address = address;
		this.rooms = rooms;
	}

	/**
	 * Function that updates the first name of the {@link Location}.
	 * It also checks if the name starts with a capital letter and throws an
	 * {@link InvalidNameStartException exception} if not.
	 *
	 * @param name the to-be set name of the location.
	 * @throws InvalidNameStartException when the input name does not start with a capital,
	 *                                   it throws this exception.
	 */
	public void setName(String name) {
		char[] nameChars = name.strip().toCharArray();

		if (!Character.isUpperCase(nameChars[0]))
			throw new InvalidNameStartException();

		this.name = name;
	}
}
