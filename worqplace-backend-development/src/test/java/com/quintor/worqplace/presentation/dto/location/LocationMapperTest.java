package com.quintor.worqplace.presentation.dto.location;

import com.quintor.worqplace.domain.Address;
import com.quintor.worqplace.domain.Location;
import com.quintor.worqplace.domain.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocationMapperTest {

	private final Address address = new Address(10, "", "LolaStreet", "1203LK", "PippiCity");
	private final List<Room> rooms = List.of(new Room(1L, 1, null, 3, Collections.emptyList()));
	private final Location location = new Location(1L, "TestLocation", address, rooms);

	@BeforeEach
	void init() {
		rooms.get(0).setLocation(location);
	}

	@Test
	@DisplayName("LocationDto should not be null")
	void dtoShouldNotBeNull() {
		var locationDTO = LocationMapper.INSTANCE.toLocationDTO(location);

		assertNotNull(locationDTO);
	}

	@Test
	@DisplayName("LocationDto should contain the correct name")
	void shouldContainCorrectName() {
		var locationDTO = LocationMapper.INSTANCE.toLocationDTO(location);

		assertEquals("TestLocation", locationDTO.getName());
	}

	@Test
	@DisplayName("LocationDto should contain the correct id")
	void shouldContainCorrectId() {
		var locationDTO = LocationMapper.INSTANCE.toLocationDTO(location);

		assertEquals(1L, locationDTO.getId());
	}

	@Test
	@DisplayName("LocationDto should contain the correct address")
	void shouldContainCorrectAddress() {
		var locationDTO = LocationMapper.INSTANCE.toLocationDTO(location);

		assertEquals(address, locationDTO.getAddress());
	}

	@Test
	@DisplayName("LocationDto should return null if location is null")
	void shouldReturnNullWhenLocationIsNull() {
		assertNull(LocationMapper.INSTANCE.toLocationDTO(null));
	}
}