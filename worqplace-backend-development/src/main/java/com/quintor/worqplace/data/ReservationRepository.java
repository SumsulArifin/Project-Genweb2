package com.quintor.worqplace.data;

import com.quintor.worqplace.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository class that connects the {@link Reservation}
 * application to the database to store the domain.
 *
 * @see Reservation
 * @see com.quintor.worqplace.application.ReservationService ReservationService
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	/**
	 * Function that retrieves all {@link Reservation reservations} by the entered
	 * employee id.
	 *
	 * @param id id of the {@link com.quintor.worqplace.domain.Employee employee}.
	 * @return a list of {@link Reservation reservations} made by the
	 * selected {@link com.quintor.worqplace.domain.Employee employee}.
	 * @see Reservation
	 * @see com.quintor.worqplace.application.ReservationService ReservationService
	 * @see com.quintor.worqplace.domain.Employee Employee
	 */
	List<Reservation> findAllByEmployeeId(Long id);
}
