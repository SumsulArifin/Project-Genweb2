package com.quintor.worqplace.application;

import com.quintor.worqplace.application.exceptions.LocationNotFoundException;
import com.quintor.worqplace.data.LocationRepository;
import com.quintor.worqplace.domain.Address;
import com.quintor.worqplace.domain.Location;
import com.quintor.worqplace.domain.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocationServiceTest {
	private LocationService locationService;
	private LocationRepository locationRepository;
	private Address address;
	private Address address1;
	private Location location;
	private Location location1;

	@BeforeEach
	void initialize() {
		address = new Address(12, "", "TestStraat", "2091BC", "QuintorCity");
		address1 = new Address(12, "", "TestStraat", "2091BC", "QuintorCity");

		this.location = new Location(1L, "Test - Quintor", address, List.of(new Room()));
		this.location1 = new Location(2L, "Test1 - Quintor", address1, List.of(new Room()));

		this.locationRepository = mock(LocationRepository.class);
		this.locationService = new LocationService(locationRepository);

		when(this.locationRepository.findAll()).thenReturn(List.of(location, location1));
		when(this.locationRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(location));
		when(this.locationRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(location1));
	}

	@Test
	@DisplayName("getAllLocations() should return a list of locations if they exist")
	void getAllLocationsShouldReturnListOfLocations() {
		assertEquals(List.of(location, location1), locationService.getAllLocations());
	}

	@Test
	@DisplayName("getAllLocations() should return an empty list of locations if none exists")
	void getAllLocationsShouldReturnEmptyListIfNoneExists() {
		when(this.locationRepository.findAll()).thenReturn(List.of());
		assertEquals(List.of(), locationService.getAllLocations());
	}

	@Test
	@DisplayName("getLocationById() should return a location if it exists")
	void getLocationByIdShouldReturnLocation() {
		assertEquals(location, locationService.getLocationById(location.getId()));
	}

	@Test
	@DisplayName("getLocationById() should throw LocationNotFoundException if no location is found")
	void getLocationByIdShouldThrowIfNotFound() {
		assertThrows(LocationNotFoundException.class, () -> locationService.getLocationById(3L));
	}
}
