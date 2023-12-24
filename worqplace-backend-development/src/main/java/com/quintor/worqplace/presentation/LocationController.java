package com.quintor.worqplace.presentation;

import com.quintor.worqplace.application.LocationService;
import com.quintor.worqplace.presentation.dto.location.LocationDTO;
import com.quintor.worqplace.presentation.dto.location.LocationMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for {@link com.quintor.worqplace.domain.Location locations}, contains logic
 * to get all locations.
 *
 * @see LocationService
 * @see com.quintor.worqplace.domain.Location Location
 */
@RestController
@RequestMapping("/locations")
@AllArgsConstructor
public class LocationController {
	private final LocationService locationService;
	private final LocationMapper locationMapper;

	/**
	 * Function that calls to the {@link LocationService} to get all
	 * {@link com.quintor.worqplace.domain.Location locations} and then maps
	 * them to a {@link com.quintor.worqplace.presentation.dto.location.LocationDTO
	 * LocationDTO}.
	 *
	 * @return a ResponseEntity containing a list of
	 * {@link com.quintor.worqplace.presentation.dto.location.LocationDTO LocationDTOs}.
	 */
	@GetMapping
	public ResponseEntity<List<LocationDTO>> getAllLocations() {
		return new ResponseEntity<>(
				locationService
						.getAllLocations()
						.stream()
						.map(locationMapper::toLocationDTO)
						.collect(Collectors.toList()),
				HttpStatus.OK
		);
	}
}
