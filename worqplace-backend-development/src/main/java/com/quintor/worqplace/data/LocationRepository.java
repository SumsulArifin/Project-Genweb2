package com.quintor.worqplace.data;

import com.quintor.worqplace.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class that connects the {@link Location}
 * application to the database to store the domain.
 *
 * @see Location
 * @see com.quintor.worqplace.application.LocationService LocationService
 */
public interface LocationRepository extends JpaRepository<Location, Long> {

}
