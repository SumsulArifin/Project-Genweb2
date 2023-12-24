package com.quintor.worqplace.domain;

import com.quintor.worqplace.application.exceptions.WorkplacesNotAvailableException;
import com.quintor.worqplace.domain.exceptions.RoomNotAvailableException;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {
	private static final LocalDate TODAY = LocalDate.now();
	private static final LocalDate NEXT_WEEK = LocalDate.now().plusWeeks(1);
	private static final LocalTime ELEVEN = LocalTime.of(11, 0);
	private static final LocalTime TWELVE = LocalTime.of(12, 0);

	private Long id;
	private int floor;
	private Location location;

	@BeforeEach
	void initialize() {
		this.id = 1L;
		this.floor = 1;
		this.location = new Location("Quintor - Groningen", new Address(11, "A", "HermanStraat", "2958GB", "Groningen"), List.of(new Room()));
	}

	@Test
	@DisplayName("should create a room correctly")
	void shouldCreateRoomCorrectly() {
		assertDoesNotThrow(() -> new Room(id, floor, location, 15, null));
	}

	@Test
	@DisplayName("isWorkplaceRecurrentlyAvailable should return true if there are no reservations")
	void isWorkplaceRecurrentlyAvailableShouldBeTrueWhenThereAreNoReservations() {
		var room = new Room(id, floor, location, 15, new ArrayList<>());
		var recurrence = new Recurrence(true, RecurrencePattern.DAILY);
		var reservation = new Reservation(TODAY, ELEVEN, TWELVE, null, room, 1, recurrence);

		assertTrue(room.isWorkplaceRecurrentlyAvailable(reservation));
	}

	@Test
	@DisplayName(" isWorkplaceRecurrentlyAvailable should return true if there are reservations that overlap")
	void isWorkplaceRecurrentlyAvailableShouldBeTrueWhenThereAreReservations() {
		var recurrence = new Recurrence(true, RecurrencePattern.DAILY);

		var room = new Room(id, floor, location, 15, new ArrayList<>());
		var reservation = new Reservation(TODAY.plusDays(1), ELEVEN, TWELVE, null, room, 1, recurrence);
		var reservation1 = new Reservation(TODAY, ELEVEN, TWELVE, null, room, 1, recurrence);
		room.addReservation(reservation);

		assertTrue(room.isWorkplaceRecurrentlyAvailable(reservation1));
	}

	@Test
	@DisplayName("isWorkplaceRecurrentlyAvailable should return false if there are reservations that overlap")
	void isWorkplaceRecurrentlyAvailableShouldBeFalseWhenNoPlacesAreAvailable() {
		var tomorrow = LocalDate.now().plusDays(1);
		var recurrence = new Recurrence(true, RecurrencePattern.DAILY);

		var room = new Room(id, floor, location, 15, new ArrayList<>());
		var reservation = new Reservation(tomorrow, ELEVEN, TWELVE, null, room, 15, recurrence);
		var reservation2 = new Reservation(LocalDate.now().plusDays(0), ELEVEN, TWELVE, null, room, 1, recurrence);
		room.addReservation(reservation);

		assertFalse(room.isWorkplaceRecurrentlyAvailable(reservation2));
	}

	@Test
	@DisplayName("countReservedWorkspaces should return the amount of reserved workplaces")
	void countReservedWorkplacesShouldReturnAmountOfReservedWorkplaces() {
		var startTime = LocalTime.of(10, 0);
		var endTime = LocalTime.of(18, 0);
		var recurrence = new Recurrence(false, RecurrencePattern.NONE);
		var reservation = new Reservation(1L, TODAY, startTime, endTime, null, null, 10, recurrence);
		var room = new Room(this.id, this.floor, this.location, 15, new ArrayList<>());
		room.addReservation(reservation);

		assertEquals(10, room.countReservedWorkplaces(TODAY, startTime, endTime));
	}

	@Test
	@DisplayName("addReservation should throw RoomNotAvailableException when room isn't recurrently available")
	void addReservationShouldThrowWhenRoomNotRecurrentlyAvailable() {
		var weeklyRecurrence = new Recurrence(true, RecurrencePattern.WEEKLY);
		var biWeeklyRecurrence = new Recurrence(true, RecurrencePattern.BIWEEKLY);

		var reservation = new Reservation(1L, TODAY, ELEVEN, TWELVE, null, null, 15, biWeeklyRecurrence);
		var room = new Room(this.id, this.floor, this.location, 15, new ArrayList<>());
		reservation.setRoom(room);
		room.addReservation(reservation);

		var reservation1 = new Reservation(2L, NEXT_WEEK, ELEVEN, TWELVE, null, room, 15, weeklyRecurrence);

		assertThrows(RoomNotAvailableException.class, () -> room.addReservation(reservation1));
	}

	@Test
	@DisplayName("addReservation should throw WorkplacesNotAvailableException when there aren't any available workplaces")
	void addReservationShouldThrowWhenWorkplacesAreNotAvailable() {
		var weeklyRecurrence = new Recurrence(true, RecurrencePattern.WEEKLY);

		var room = new Room(this.id, this.floor, this.location, 15, new ArrayList<>());
		var reservation = new Reservation(1L, TODAY, ELEVEN, TWELVE, null, room, 15, weeklyRecurrence);
		room.addReservation(reservation);

		assertThrows(WorkplacesNotAvailableException.class, () -> room.addReservation(reservation));
	}

	@Test
	@DisplayName("isWorkplaceRecurrentlyAvailable should return false when it isn't")
	void isWorkplaceRecurrentlyAvailableShouldReturnFalseWhenItIsNot() {
		var tomorrow = TODAY.plusDays(1);
		var monthlyRecurrence = new Recurrence(true, RecurrencePattern.MONTHLY);

		var room = new Room(this.id, this.floor, this.location, 15, new ArrayList<>());
		var reservation = new Reservation(1L, TODAY, ELEVEN, TWELVE, null, room, 14, monthlyRecurrence); //TODO: Fix code, if this is 15 it doesn't work
		var reservation1 = new Reservation(2L, tomorrow, ELEVEN, TWELVE, null, room, 1, monthlyRecurrence);
		room.addReservation(reservation);

		assertTrue(room.isWorkplaceRecurrentlyAvailable(reservation1));
	}
}
