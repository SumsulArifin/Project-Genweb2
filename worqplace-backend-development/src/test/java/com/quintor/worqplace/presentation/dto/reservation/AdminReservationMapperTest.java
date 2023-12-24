package com.quintor.worqplace.presentation.dto.reservation;

import com.quintor.worqplace.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AdminReservationMapperTest {

	private static final LocalDate TODAY = LocalDate.now();
	private static final LocalTime STARTTME = LocalTime.of(9, 0);
	private static final LocalTime ENDTIME = LocalTime.of(17, 0);
	private static final Employee EMPLOYEE = new Employee(1L, "Lola", "Hoeing");
	private static final Room ROOM = new Room(1L, 3, null, 4, Collections.emptyList());
	private static final Recurrence RECURRENCE = new Recurrence(false, RecurrencePattern.NONE);
	private static final Reservation RESERVATION = new Reservation(1L, TODAY, STARTTME, ENDTIME, EMPLOYEE, ROOM, 1, RECURRENCE);

	@Test
	@DisplayName("adminReservationDTO should not be null")
	void reservationDtoShouldNotBeNull() {
		var dto = AdminReservationMapper.INSTANCE.toAdminReservationDTO(RESERVATION);

		assertNotNull(dto);
	}

	@Test
	@DisplayName("adminReservationDTO should have the correct id")
	void reservationDtoShouldHaveCorrectId() {
		var dto = AdminReservationMapper.INSTANCE.toAdminReservationDTO(RESERVATION);

		assertEquals(RESERVATION.getId(), dto.getId());
	}

	@Test
	@DisplayName("adminReservationDTO should have the correct date")
	void reservationDtoShouldHaveCorrectDate() {
		var dto = AdminReservationMapper.INSTANCE.toAdminReservationDTO(RESERVATION);

		assertEquals(RESERVATION.getDate(), dto.getDate());
	}

	@Test
	@DisplayName("adminReservationDTO should have the correct starttime")
	void reservationDtoShouldHaveCorrectStartTime() {
		var dto = AdminReservationMapper.INSTANCE.toAdminReservationDTO(RESERVATION);
		assertEquals(RESERVATION.getStartTime(), dto.getStartTime());

	}

	@Test
	@DisplayName("adminReservationDTO should have the correct endtime")
	void reservationDtoShouldHaveCorrectEndTime() {
		var dto = AdminReservationMapper.INSTANCE.toAdminReservationDTO(RESERVATION);
		assertEquals(RESERVATION.getEndTime(), dto.getEndTime());

	}

	@Test
	@DisplayName("adminReservationDTO should have the correct employee first name")
	void reservationDtoShouldHaveCorrectEmployeeFirstName() {
		var dto = AdminReservationMapper.INSTANCE.toAdminReservationDTO(RESERVATION);
		assertEquals(RESERVATION.getEmployee().getFirstName(), dto.getEmployeeFirstName());
	}

	@Test
	@DisplayName("adminReservationDTO should have the correct employee last name")
	void reservationDtoShouldHaveCorrectEmployeeLastName() {
		var dto = AdminReservationMapper.INSTANCE.toAdminReservationDTO(RESERVATION);
		assertEquals(RESERVATION.getEmployee().getLastName(), dto.getEmployeeLastName());
	}

	@Test
	@DisplayName("adminReservationDTO should have the correct room id")
	void reservationDtoShouldHaveCorrectRoomId() {
		var dto = AdminReservationMapper.INSTANCE.toAdminReservationDTO(RESERVATION);
		assertEquals(RESERVATION.getRoom().getId(), dto.getRoomId());

	}

	@Test
	@DisplayName("adminReservationDTO should have the correct recurrence")
	void reservationDtoShouldHaveCorrectRecurrence() {
		var dto = AdminReservationMapper.INSTANCE.toAdminReservationDTO(RESERVATION);
		assertEquals(RESERVATION.getRecurrence(), dto.getRecurrence());
	}

	@Test
	@DisplayName("adminReservationDTO should return null when location is null")
	void reservationDtoShouldReturnNullWhenLocationIsNull() {
		assertNull(AdminReservationMapper.INSTANCE.toAdminReservationDTO(null));
	}

}