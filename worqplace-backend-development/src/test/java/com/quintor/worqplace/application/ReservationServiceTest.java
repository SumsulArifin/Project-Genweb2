package com.quintor.worqplace.application;

import com.quintor.worqplace.application.exceptions.*;
import com.quintor.worqplace.data.EmployeeRepository;
import com.quintor.worqplace.data.LocationRepository;
import com.quintor.worqplace.data.ReservationRepository;
import com.quintor.worqplace.data.RoomRepository;
import com.quintor.worqplace.domain.*;
import com.quintor.worqplace.presentation.dto.reservation.ReservationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReservationServiceTest {
	private ReservationService reservationService;

	private RoomRepository roomRepository;
	private LocationRepository locationRepository;
	private EmployeeRepository employeeRepository;
	private ReservationRepository reservationRepository;

	private Room room;
	private Room room1;
	private Employee employee;
	private Location location;
	private Location location1;
	private Recurrence noRecurrence;
	private Reservation reservation;
	private Reservation reservation1;
	private Reservation reservation2;
	private Reservation reservation3;
	private Reservation reservation4;
	private Reservation reservation5;

	@BeforeEach
	void initialize() {
		this.roomRepository = mock(RoomRepository.class);
		this.employeeRepository = mock(EmployeeRepository.class);
		this.locationRepository = mock(LocationRepository.class);
		this.reservationRepository = mock(ReservationRepository.class);

		EmployeeService employeeService = new EmployeeService(employeeRepository);
		LocationService locationService = new LocationService(locationRepository);
		RoomService roomService = new RoomService(roomRepository, locationService);
		this.reservationService = new ReservationService(employeeService, roomService, reservationRepository);

		this.employee = new Employee(1L, "QFirstname", "QLastname");
		Address address = new Address(12, "", "TestStreet", "2098GS", "QuintorCity");
		Address address1 = new Address(13, "", "TestStreeto", "2038GS", "QuintorCita");

		this.room = new Room(1L, 1, null, 15, Collections.emptyList());
		this.room1 = new Room(2L, 1, null, 15, Collections.emptyList());
		this.location = new Location(1L, "QuintorTest", address, List.of(room));
		this.location1 = new Location(2L, "Quintor2", address1, List.of(room1));
		this.noRecurrence = new Recurrence(false, RecurrencePattern.NONE);
		this.reservation = new Reservation(1L, LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(15, 0), employee, null, 15, noRecurrence);
		this.reservation1 = new Reservation(2L, LocalDate.now().plusDays(4), LocalTime.of(12, 0), LocalTime.of(13, 0), employee, room, 15, noRecurrence);
		this.reservation2 = new Reservation(3L, LocalDate.now().plusMonths(3), LocalTime.of(9, 36), LocalTime.of(13, 10), employee, room, 15, noRecurrence);
		this.reservation3 = new Reservation(4L, LocalDate.now().plusDays(1), LocalTime.of(18, 0), LocalTime.of(19, 0), employee, room, 15, noRecurrence);
		this.reservation4 = new Reservation(5L, LocalDate.now().plusDays(1), LocalTime.of(18, 0), LocalTime.of(19, 0), employee, room1, 15, noRecurrence);
		this.reservation5 = new Reservation(6L, LocalDate.now().minusDays(1), LocalTime.of(18, 0), LocalTime.of(19, 0), employee, room1, 15, noRecurrence, true);
		this.room.setLocation(location);
		this.room1.setLocation(location1);
		this.room.setReservations(List.of(reservation1));
		this.room1.setReservations(List.of(reservation2, reservation3));

		setRepositoryBehaviour();
	}

	@Test
	@DisplayName("getAllReservations() should return all reservations if there are any")
	void getAllReservationsShouldReturnAllReservations() {
		assertEquals(List.of(reservation, reservation1, reservation5), reservationService.getAllReservations(true));
	}

	@Test
	@DisplayName("getAllReservations() should return only active reservations when includeOld is false")
	void getAllReservationsWithoutOldReservationsShouldReturnOnlyActiveReservations() {
		assertEquals(List.of(reservation, reservation1), reservationService.getAllReservations(false));
	}

	@Test
	@DisplayName("getReservationById() should return a reservations if it exists")
	void getReservationByIdShouldReturnReservation() {
		assertEquals(reservation, reservationService.getReservationById(reservation.getId()));
	}

	@Test
	@DisplayName("getReservationById() should throw ReservationNotFoundException if it isn't found")
	void getReservationByIdShouldThrowIfNotFound() {
		assertThrows(ReservationNotFoundException.class, () -> reservationService.getReservationById(3L));
	}

	@Test
	@DisplayName("reserveWorkplace() should Throw WorkplacesNotAvailableException if it isn't available")
	void reserveWorkplaceShouldThrowWhenWorkplaceIsNotAvailable() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setId(1L);
		reservationDTO.setDate(reservation.getDate());
		reservationDTO.setStartTime(reservation.getStartTime());
		reservationDTO.setEndTime(reservation.getEndTime());
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setWorkplaceAmount(15);
		reservationDTO.setRoomId(1L);
		reservationDTO.setRecurrence(this.noRecurrence);
		reservationService.reserveWorkplaces(reservationDTO);
		reservationDTO.setId(2L);

		assertThrows(WorkplacesNotAvailableException.class, () -> reservationService.reserveWorkplaces(reservationDTO));
	}

	@Test
	@DisplayName("reserveWorkplace() should throw InvalidStartAndEndTimeException when End time is after Begin time")
	void reserveWorkplaceShouldThrowWhenEndTimeIsAfterBeginTime() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setId(5L);
		reservationDTO.setDate(reservation.getDate());
		reservationDTO.setStartTime(reservation.getEndTime());
		reservationDTO.setEndTime(reservation.getStartTime());
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(1L);
		reservationDTO.setWorkplaceAmount(15);
		reservationDTO.setRecurrence(this.noRecurrence);

		assertThrows(InvalidStartAndEndTimeException.class, () -> reservationService.reserveWorkplaces(reservationDTO));

	}

	@Test
	@DisplayName("reserveWorkplace() should throw InvalidDayException if date is before today")
	void reserveWorkplaceShouldThrowWhenDateIsBeforeToday() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setId(8L);
		reservationDTO.setDate(LocalDate.now().minusDays(5));
		reservationDTO.setStartTime(LocalTime.of(8, 0));
		reservationDTO.setEndTime(LocalTime.of(19, 3));
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setWorkplaceAmount(15);
		reservationDTO.setRecurrence(this.noRecurrence);
		reservationDTO.setRoomId(1L);

		assertThrows(InvalidDayException.class, () -> reservationService.reserveWorkplaces(reservationDTO));
	}

	@Test
	@DisplayName("toReservation() should convert DTO to reservation correctly")
	void toReservationShouldConvertCorrectly() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setId(8L);
		reservationDTO.setDate(reservation2.getDate());
		reservationDTO.setStartTime(reservation2.getStartTime());
		reservationDTO.setEndTime(reservation2.getEndTime());
		reservationDTO.setEmployeeId(reservation2.getEmployee().getId());
		reservationDTO.setWorkplaceAmount(15);
		reservationDTO.setRoomId(1L);
		reservationDTO.setRecurrence(this.noRecurrence);

//		Multiple assertions because we can't create the same object twice given the Id is autogenerated
//		This way all the properties are tested excluding the id.
		assertEquals(reservation2.getDate(), reservationService.toReservation(reservationDTO).getDate());
		assertEquals(reservation2.getRoom(), reservationService.toReservation(reservationDTO).getRoom());
		assertEquals(reservation2.getEmployee(), reservationService.toReservation(reservationDTO).getEmployee());
		assertEquals(reservation2.getStartTime(), reservationService.toReservation(reservationDTO).getStartTime());
		assertEquals(reservation2.getEndTime(), reservationService.toReservation(reservationDTO).getEndTime());
		assertEquals(reservation2.getWorkplaceAmount(), reservationService.toReservation(reservationDTO).getWorkplaceAmount());
	}

	@Test
	@DisplayName("reserveRoom() should throw RoomNotFoundException when room is null")
	void reserveRoomShouldThrowWhenRoomIsNull() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setId(8L);
		reservationDTO.setDate(reservation2.getDate());
		reservationDTO.setStartTime(reservation2.getStartTime());
		reservationDTO.setEndTime(reservation2.getEndTime());
		reservationDTO.setEmployeeId(reservation2.getEmployee().getId());
		reservationDTO.setRecurrence(this.noRecurrence);

		assertThrows(RoomNotFoundException.class, () -> reservationService.reserveRoom(reservationDTO));
	}

	@Test
	@DisplayName("reserveRoom() should throw WorkplacesNotAvailable if room is not available")
	void reserveRoomShouldThrowIfItNotAvailable() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setId(8L);
		reservationDTO.setDate(reservation1.getDate());
		reservationDTO.setStartTime(reservation1.getStartTime());
		reservationDTO.setEndTime(reservation1.getEndTime());
		reservationDTO.setEmployeeId(reservation1.getEmployee().getId());
		reservationDTO.setRoomId(reservation1.getRoom().getId());
		reservationDTO.setRecurrence(this.noRecurrence);

		assertThrows(WorkplacesNotAvailableException.class, () -> reservationService.reserveRoom(reservationDTO));
	}

	@Test
	@DisplayName("reserveRoom() should reserve the room if it's available")
	void reserveRoomExecutesWhenAvailable() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setId(reservation3.getId());
		reservationDTO.setDate(reservation3.getDate());
		reservationDTO.setStartTime(reservation3.getStartTime());
		reservationDTO.setEndTime(reservation3.getEndTime());
		reservationDTO.setEmployeeId(reservation3.getEmployee().getId());
		reservationDTO.setRoomId(reservation3.getRoom().getId());
		reservationDTO.setRecurrence(reservation3.getRecurrence());

		assertDoesNotThrow(() -> reservationService.reserveRoom(reservationDTO));
	}

	@Test
	@DisplayName("getAllMyReservations should return all reservations made by the given user")
	void getAllMyReservationsReturnsAllReservationsByUser() {
		assertEquals(3, reservationService.getAllMyReservations(this.employee.getId(), null, null).size());
	}

	@Test
	@DisplayName("getAllByLocation should return all reservations for a location")
	void getAllByLocation() {
		assertEquals(2, reservationService.getAllByLocation(location1.getId()).size());
	}

	@Test
	@DisplayName("deleteReservation should delete reservation")
	void deleteReservation() {
		var reservationId = reservation4.getId();
		reservationService.deleteReservation(reservationId);
		assertThrows(ReservationNotFoundException.class,
				() -> reservationService.getReservationById(reservationId));
	}

	/**
	 * Sets mock behaviour for repositories used in this test file
	 */
	private void setRepositoryBehaviour() {
//		Reservation repository
		when(reservationRepository.findById(3L)).thenReturn(Optional.empty());
		when(reservationRepository.save(reservation)).thenReturn(reservation);
		when(reservationRepository.findAll()).thenReturn(List.of(reservation, reservation1, reservation5));
		when(reservationRepository.findById(reservation.getId())).thenReturn(java.util.Optional.ofNullable(reservation));
		when(reservationRepository.findAllByEmployeeId(employee.getId()))
				.thenReturn(List.of(reservation, reservation1, reservation2));
		when(reservationRepository.findById(reservation4.getId())).thenReturn(Optional.empty());

//		Employee repository
		when(employeeRepository.findById(1L)).thenReturn(Optional.ofNullable(employee));

//		Room repository
		when(roomRepository.findById(room.getId())).thenReturn(Optional.ofNullable(room));
		when(roomRepository.findById(room1.getId())).thenReturn(Optional.ofNullable(room1));

//		Location repository
		when(locationRepository.findById(location.getId())).thenReturn(Optional.ofNullable(location));
		when(locationRepository.findById(location1.getId())).thenReturn(Optional.ofNullable(location1));
	}
}
