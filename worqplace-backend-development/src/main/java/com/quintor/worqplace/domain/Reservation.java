package com.quintor.worqplace.domain;

import com.quintor.worqplace.application.exceptions.InvalidDayException;
import com.quintor.worqplace.application.exceptions.InvalidStartAndEndTimeException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Class containing {@link Reservation} data like the date, start time and end time,
 * {@link Employee}, {@link Room}, amount of workplaces and {@link Recurrence}.
 *
 * @see Employee
 * @see Room
 * @see Recurrence
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reservation")
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "room_id")
	private Room room;

	private int workplaceAmount;

	@Embedded
	private Recurrence recurrence;

	/**
	 * Constructor of the {@link Reservation} class, used for testing purposes and
	 * by Spring, calls
	 * {@link #Reservation(LocalDate, LocalTime, LocalTime, Employee, Room, int, Recurrence)}
	 * and then sets the input id manually.
	 *
	 * @param id         id of the reservation generated by Spring.
	 * @param date       reservation date.
	 * @param startTime  start time of reservation.
	 * @param endTime    end time of reservation.
	 * @param employee   employee that places the reservation.
	 * @param room       room that is being reserved.
	 * @param amount     amount of workplaces that is being reserved.
	 * @param recurrence handles the {@link Recurrence recurrence} of the reservation.
	 * @implNote is used by Spring for retrieving reservations.
	 */
	@SuppressWarnings("squid:S00107") // Suppresses the too many parameters warning as the id is used for testing.
	public Reservation(Long id, LocalDate date, LocalTime startTime, LocalTime endTime,
	                   Employee employee, Room room, int amount, Recurrence recurrence) {
		this(date, startTime, endTime, employee, room, amount, recurrence);
		this.id = id;
	}

	/**
	 * Constructor of the {@link Reservation} class, used for testing purposes of older reservations.
	 *
	 * @param id                id of the reservation generated by Spring.
	 * @param date              reservation date.
	 * @param startTime         start time of reservation.
	 * @param endTime           end time of reservation.
	 * @param employee          employee that places the reservation.
	 * @param room              room that is being reserved.
	 * @param amount            amount of workplaces that is being reserved.
	 * @param recurrence        handles the {@link Recurrence recurrence} of the reservation.
	 * @param legacyReservation a variable to make clear that this is a legacy reservation.
	 * @implNote is used by Spring for retrieving reservations.
	 */
	@SuppressWarnings("squid:S00107") // Suppresses the too many parameters warning as the id is used for testing.
	public Reservation(Long id, LocalDate date, LocalTime startTime, LocalTime endTime,
	                   Employee employee, Room room, int amount, Recurrence recurrence, boolean legacyReservation) {
		this.id = id;
		if (legacyReservation) {
			this.date = date;
		} else {
			if (startTime.isAfter(endTime)) throw new InvalidStartAndEndTimeException();
			setDate(date);
		}
		this.startTime = startTime;
		this.endTime = endTime;
		this.employee = employee;
		this.room = room;
		this.workplaceAmount = amount;
		this.recurrence = recurrence;
	}

	/**
	 * Main constructor of the {@link Reservation} class. Calls the setter method
	 * of the date to validate it.
	 *
	 * @param date       reservation date.
	 * @param startTime  start time of reservation.
	 * @param endTime    end time of reservation.
	 * @param employee   employee that places the reservation.
	 * @param room       room that is being reserved.
	 * @param amount     amount of workplaces that is being reserved.
	 * @param recurrence handles the {@link Recurrence recurrence} of the reservation.
	 */
	public Reservation(LocalDate date, LocalTime startTime, LocalTime endTime,
	                   Employee employee, Room room, int amount, Recurrence recurrence) {
		if (startTime.isAfter(endTime))
			throw new InvalidStartAndEndTimeException();

		this.startTime = startTime;
		this.endTime = endTime;
		setDate(date);
		this.employee = employee;
		this.room = room;
		this.workplaceAmount = amount;
		this.recurrence = recurrence;
	}

	/**
	 * Sets the date of the {@link Reservation} but first checks the date to
	 * make sure it is not before today and throws {@link InvalidDayException exception} if not.
	 *
	 * @param date date of the reservation.
	 * @throws InvalidDayException when the day if before today.
	 */
	public void setDate(LocalDate date) {
		if (date.isBefore(LocalDate.now()))
			throw new InvalidDayException();

		this.date = date;
	}

	/**
	 * Function that returns whether this reservation has passed or is yet to come.
	 *
	 * @param date date on which to check if it is active
	 * @return a {@link Boolean} that which is true if the reservation will occur in the future,
	 * or has already happened.
	 */
	public boolean isReservationActive(LocalDate date) {
		return this.date.compareTo(date) >= 0 || this.recurrence.isActive();
	}

	@Override
	public String toString() {
		return "Reservation{" +
				"id=" + id +
				", date=" + date +
				", startTime=" + startTime +
				", endTime=" + endTime +
				", employee=" + employee.getId() +
				", room=" + room.getId() +
				", workplaceAmount=" + workplaceAmount +
				", recurring=" + recurrence +
				'}';
	}
}