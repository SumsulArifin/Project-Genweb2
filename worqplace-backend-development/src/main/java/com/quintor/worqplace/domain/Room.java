package com.quintor.worqplace.domain;

import com.quintor.worqplace.application.exceptions.WorkplacesNotAvailableException;
import com.quintor.worqplace.application.util.DateTimeUtils;
import com.quintor.worqplace.domain.exceptions.RoomNotAvailableException;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Room class, contains data regarding capacity and location.
 *
 * @see Location
 * @see Reservation
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room")
public class Room {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int floor;

	@ManyToOne(fetch = FetchType.LAZY)
	private Location location;

	private int capacity;

	@OneToMany(mappedBy = "room")
	private List<Reservation> reservations;

	/**
	 * Function that counts the amount of reserved workplaces during a specific timeslot.
	 *
	 * @param date      date to check the reserved workplaces on.
	 * @param startTime start time of the timeslot of which to check the reserved workplaces on.
	 * @param endTime   end time of the timeslot of which to check the reserved workplaces on.
	 * @return an int representing the amount of reserved workplaces.
	 */
	public int countReservedWorkplaces(LocalDate date, LocalTime startTime, LocalTime endTime) {
		return countReservedWorkplaces(date, startTime, endTime, null);
	}

	public int countReservedWorkplaces(Reservation reservation) {
		return countReservedWorkplaces(reservation.getDate(), reservation.getStartTime(), reservation.getEndTime(), null);
	}

	/**
	 * Function that counts the amount of reserved workplaces during a specific timeslot at a given date.
	 * Can also exclude a reservation if one is given.
	 *
	 * @param date                {@link LocalDate date} to check the reserved workplaces on.
	 * @param startTime           {@link LocalTime Start time} of the timeslot which to check the reserved workplaces for.
	 * @param endTime             {@link LocalTime End time} of the timeslot which to check the reserved workplaces for.
	 * @param excludedReservation {@link Reservation} reservation that needs to be excluded in the evaluation (used for updating a reservation)
	 * @return the amount of reserved workplaces.
	 */
	public int countReservedWorkplaces(LocalDate date, LocalTime startTime, LocalTime endTime, @Nullable Reservation excludedReservation) {
		// If there are no reservations that need to be excluded it will just use the reservations.
		var usedReservations = excludedReservation == null ? this.getReservations() :
				this.getReservations().stream().filter(reservation ->
						!reservation.getId().equals(excludedReservation.getId())).toList();

		return usedReservations.stream().filter(reservation ->
				reservation.isReservationActive(date) &&
				(date.isAfter(reservation.getDate()) ||
						date.isEqual(reservation.getDate())) &&
						DateTimeUtils.timeslotsOverlap(reservation.getDate(),
								reservation.getStartTime(), reservation.getEndTime(),
								reservation.getRecurrence(), date, startTime, endTime))
				.mapToInt(Reservation::getWorkplaceAmount).sum();
	}

	/**
	 * Function that checks if the requested amount of workplaces is available.
	 *
	 * @param reservation Reservation for which to check the availability.
	 * @return a boolean indicating the availability.
	 */
	public boolean isWorkplaceRecurrentlyAvailable(Reservation reservation) {
		return isWorkplaceRecurrentlyAvailable(reservation, null);
	}

	/**
	 * Function that checks if the requested amount of workplaces is available. Can also exclude reservations when given one.
	 *
	 * @param oldReservation {@link Reservation} for which to check the availability if newReservation is null.
	 * @param newReservation Reservation for which to check the availability.
	 * @return a boolean indicating the availability.
	 */
	public boolean isWorkplaceRecurrentlyAvailable(Reservation oldReservation, @Nullable Reservation newReservation) {
		var reservationsClone = new ArrayList<>(this.getReservations());
		var reservationUsed = newReservation != null ? newReservation : oldReservation;
		reservationsClone.remove(reservationUsed);
		return !requestedAmountExceedsAvailableAmount(reservationUsed, reservationsClone);
	}

	/**
	 * Function that gets all the overlapping reservations and then adds the workplaces to calculate how many free
	 * ones are left. Bases on this returns if the requested amount exceeds the available amount.
	 *
	 * @param reservationUsed   {@link Reservation}
	 * @param reservationsClone {@link Reservation}
	 * @return if the requested amount exceeds the available amount of workplaces in that specific room.
	 */
	private boolean requestedAmountExceedsAvailableAmount(Reservation reservationUsed, ArrayList<Reservation> reservationsClone) {
		var total = 0;

		for (Reservation existingReservation : reservationsClone) {
			if (!existingReservation.isReservationActive(reservationUsed.getDate())) continue;
			var overlappingReservations = this.getReservationsThatOverlap(existingReservation, reservationsClone);

			for (Reservation reservation : overlappingReservations)
				total += DateTimeUtils.timeslotsOverlap(reservationUsed, reservation) ? reservation.getWorkplaceAmount() : 0;

			if ((total + reservationUsed.getWorkplaceAmount()) > this.capacity) return true;
			total = 0;
		}
		return false;
	}

	/**
	 * Function that adds a reservation to the list of reservations.
	 *
	 * @param reservation the reservation that is to be added.
	 */
	public void addReservation(Reservation reservation) {
		int wanted = reservation.getWorkplaceAmount();
		int available = this.capacity - countReservedWorkplaces(reservation);
		if (wanted > available)
			throw new WorkplacesNotAvailableException(wanted, available);

		if (!isWorkplaceRecurrentlyAvailable(reservation))
			throw new RoomNotAvailableException();

		var reservationsClone = new ArrayList<>(this.reservations);
		reservationsClone.add(reservation);
		this.setReservations(reservationsClone);
	}

	/**
	 * Function that checks if the new (updated) reservation is possible. And if so, replaces the old reservation.
	 *
	 * @param oldReservation old reservation
	 * @param newReservation new Reservation
	 */
	public void updateReservation(Reservation oldReservation, Reservation newReservation) {
		int wanted = newReservation.getWorkplaceAmount();
		int available = this.capacity - countReservedWorkplaces(newReservation.getDate(), newReservation.getStartTime(), newReservation.getEndTime(), oldReservation);

		if (wanted > available) throw new WorkplacesNotAvailableException(wanted, available);
		if (!isWorkplaceRecurrentlyAvailable(oldReservation, newReservation)) throw new RoomNotAvailableException();

		var reservationsClone = new ArrayList<>(this.reservations);
		reservationsClone.remove(oldReservation);
		reservationsClone.add(newReservation);
		this.setReservations(reservationsClone);
	}

	/**
	 * Function that gets all reservations that overlap at the given date and between the given times.
	 *
	 * @param date         Date at which the overlapping reservations should be given.
	 * @param startTime    start time of the timeslot at which overlapping reservations should be given.
	 * @param endTime      end time of the timeslot at which overlapping reservations should be given.
	 * @param reservations {@link Nullable} list of reservations is a certain list of reservations should be used, instead of the {@link Room#reservations} from the room.
	 * @return List of reservations that are overlapping at the given date and times.
	 */
	public List<Reservation> getReservationsThatOverlap(LocalDate date,
	                                                    LocalTime startTime, LocalTime endTime, @Nullable List<Reservation> reservations) {
		var reservationList = reservations != null ? reservations : this.reservations;

		return reservationList.stream()
				.filter(reservation ->
						DateTimeUtils.timeslotsOverlap(reservation.getDate(),
								reservation.getStartTime(), reservation.getEndTime(),
								reservation.getRecurrence(), date,
								startTime, endTime)).toList();
	}

	/**
	 * Function that gets all reservations that overlap with the given reservation.
	 *
	 * @param reservation  {@link Reservation} for finding already existing overlapping reservations with.
	 * @param reservations {@link Nullable} List of reservations if a certain list of reservations should be used,
	 *                     instead of the {@link Room#reservations} from the room.
	 * @return List of reservations that are overlapping with the given reservation.
	 */
	public List<Reservation> getReservationsThatOverlap(Reservation reservation, @Nullable List<Reservation> reservations) {
		List<Reservation> reservationList = reservations != null ? reservations : this.reservations;

		return this.getReservationsThatOverlap(reservation.getDate(), reservation.getStartTime(),
				reservation.getEndTime(), reservationList);
	}
}
