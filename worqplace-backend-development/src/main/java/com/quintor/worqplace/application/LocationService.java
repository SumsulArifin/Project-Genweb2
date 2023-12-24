package com.quintor.worqplace.application;

import com.quintor.worqplace.application.exceptions.LocationNotFoundException;
import com.quintor.worqplace.data.LocationRepository;
import com.quintor.worqplace.domain.Location;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Service class that handles communication to the {@link LocationRepository repository}.
 *
 * @see Location
 * @see LocationRepository
 */
@Service
@Transactional
@AllArgsConstructor
public class LocationService {
	private final LocationRepository locationRepository;

	/**
	 * Function that gets all {@link Location locations} from the database.
	 *
	 * @return a list containing all {@link Location locations}.
	 * @see Location
	 * @see LocationRepository
	 */
	public List<Location> getAllLocations() {
		return locationRepository.findAll();
	}

	/**
	 * Function that gets the requested {@link Location} by the entered id.
	 *
	 * @param id the id of the {@link Location}.
	 * @return the {@link Location} with the corresponding id.
	 * @throws LocationNotFoundException when no location in the database matches
	 *                                   the entered id, this exception is thrown.
	 * @see Location
	 * @see LocationNotFoundException
	 * @see RoomService
	 */
	public Location getLocationById(Long id) throws LocationNotFoundException {
		return locationRepository
				.findById(id)
				.orElseThrow(() -> new LocationNotFoundException(id));
	}
}
