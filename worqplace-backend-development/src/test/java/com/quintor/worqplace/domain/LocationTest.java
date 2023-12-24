package com.quintor.worqplace.domain;

import com.quintor.worqplace.domain.exceptions.InvalidNameStartException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocationTest {
	private String name;
	private Address address;
	private List<Room> rooms;

	@BeforeEach
	void initialize() {
		this.name = "Quintor - Groningen";
		this.address = new Address(11, "A", "HermanStraat", "2958GB", "Groningen");
		this.rooms = List.of(new Room(), new Room(), new Room());
	}

	@Test
	@DisplayName("Should create location correctly")
	void shouldCreateCorrectly() {
		assertDoesNotThrow(() -> new Location(name, address, rooms));
	}

	@Test
	@DisplayName("Should throw InvalidLocationNameException if location name doesn't start with a capital letter")
	void shouldThrowWhenNameDoesNotStartWithCapital() {
		assertThrows(InvalidNameStartException.class, () -> new Location("quintor", address, rooms));
	}
}
