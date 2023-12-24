package com.quintor.worqplace.domain;

import com.quintor.worqplace.application.exceptions.InvalidDayException;
import com.quintor.worqplace.application.exceptions.InvalidStartAndEndTimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {
	private LocalDate date;
	private LocalDate yesterday;
	private LocalTime startTime;
	private LocalTime endTime;
	private Employee employee;
	private Room room;
	private Recurrence noRecurrence;
	private Recurrence activeRecurrence;

	@BeforeEach
	void initialize() {
		this.date = LocalDate.now().plusDays(1);
		this.yesterday = LocalDate.now().minusDays(1);
		this.startTime = LocalTime.of(9, 0);
		this.endTime = LocalTime.of(18, 0);
		this.employee = new Employee("Test", "Test");
		this.room = new Room();
		this.noRecurrence = new Recurrence(false, null);
		this.activeRecurrence = new Recurrence(true, RecurrencePattern.DAILY);
	}

	@Test
	@DisplayName("a Reservation object should be able to be made without a room")
	void shouldCreateReservationCorrectlyWithRoomNull() {
		assertDoesNotThrow(() -> new Reservation(date, startTime, endTime, employee, null, 15, noRecurrence));
	}

	@Test
	@DisplayName("a Reservation object should be able to be made without an amount of workplaces")
	void shouldCreateReservationCorrectlyWithWorkplaceNull() {
		assertDoesNotThrow(() -> new Reservation(date, startTime, endTime, employee, room, 15, noRecurrence));
	}

	@Test
	@DisplayName("When using a custom id, a Reservation object should still be able to be made")
	void shouldCreateReservationCorrectlyWithCustomId() {
		assertDoesNotThrow(() -> new Reservation(1L, date, startTime, endTime, employee, room, 15, noRecurrence));
	}

	@Test
	@DisplayName("an exception should throw when the reservation date is before today")
	void ShouldThrowWhenDateIsBeforeToday() {
		assertThrows(InvalidDayException.class, () -> new Reservation(yesterday, startTime, endTime, employee, null, 15, noRecurrence));
	}

	@Test
	@DisplayName("an exception should throw when the reservation end time is before the reservation start time")
	void shouldThrowIfEndTimeIsBeforeStartTime() {
		var endTime = this.startTime.minusMinutes(1);
		assertThrows(InvalidStartAndEndTimeException.class, () -> new Reservation(date, startTime, endTime, employee, null, 15, noRecurrence));
	}

	@Test
	@DisplayName("isReservationActive check should return false when date has passed and recurrence is inactive")
	void isReservationActiveBeforeTodayWithoutRecurrence() {
		Reservation reservation = new Reservation(1L, yesterday, startTime, endTime, employee, room, 15, noRecurrence, true);
		assertFalse(reservation.isReservationActive(LocalDate.now()));
	}

	@Test
	@DisplayName("isReservationActive check should return true when date has passed but recurrence is active")
	void isReservationActiveBeforeTodayWithRecurrence() {
		Reservation reservation = new Reservation(1L, yesterday, startTime, endTime, employee, room, 15, activeRecurrence, true);
		assertTrue(reservation.isReservationActive(LocalDate.now()));
	}

	@Test
	@DisplayName("legacy reservation constructor should default when not a legacy reservation, date before today should throw")
	void legacyReservationConstructorDefaultDateCheck() {
		assertThrows(InvalidDayException.class, () -> new Reservation(1L, yesterday, startTime, endTime, employee, null, 15, noRecurrence, false));
	}

	@Test
	@DisplayName("legacy reservation constructor should default when not a legacy reservation, end time before start time should throw")
	void legacyReservationConstructorDefaultTimeCheck() {
		assertThrows(InvalidStartAndEndTimeException.class, () -> new Reservation(1L, date, endTime, startTime, employee, null, 15, noRecurrence, false));
	}
}
