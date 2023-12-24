package com.quintor.worqplace.application;

import com.quintor.worqplace.application.exceptions.RoomNotFoundException;
import com.quintor.worqplace.application.util.DateTimeUtils;
import com.quintor.worqplace.application.util.RoomAvailability;
import com.quintor.worqplace.data.RoomRepository;
import com.quintor.worqplace.domain.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.quintor.worqplace.application.util.DateTimeUtils.checkReservationDateTime;

/**
 * Service class that handles the communication between the
 * {@link com.quintor.worqplace.presentation.RoomController controller},
 * the domain and the {@link RoomRepository repository}.
 *
 * @see Room
 * @see com.quintor.worqplace.presentation.RoomController RoomController
 * @see RoomRepository
 * @see Location
 * @see LocationService
 */
@Service
@Transactional
public class RoomService {
	private final RoomRepository roomRepository;
	private final LocationService locationService;

	@Lazy // The locationService also depends on this service, if not lazy it can never start.
	public RoomService(RoomRepository roomRepository, LocationService locationService) {
		this.roomRepository = roomRepository;
		this.locationService = locationService;
	}

	/**
	 * Method that retrieves the availability for {@link Room} at the given {@link Location}
	 * at the given date and times.
	 *
	 * @param locationId id of the wanted room
	 * @param date       date
	 * @param startTime  start time
	 * @param endTime    end time
	 * @param recurrencePattern the pattern of {@link Recurrence} for which to check
	 * @return a {@link List} of {@link RoomAvailability}.
	 * @see RoomAvailability
	 */
	public List<RoomAvailability> getRoomsAvailabilityAtDateTime(Long locationId, LocalDate date,
	                                                             LocalTime startTime, LocalTime endTime,
	                                                             RecurrencePattern recurrencePattern) {
		var recurrence = new Recurrence(recurrencePattern != RecurrencePattern.NONE, recurrencePattern);
		var rooms = getRoomsAvailableAtDateTime(locationId, date, startTime, endTime);
		rooms = rooms.stream().filter(room -> !recurrence.isActive() || room.isWorkplaceRecurrentlyAvailable(
						new Reservation(date, startTime, endTime, null, room, room.getCapacity(), recurrence)))
				.collect(Collectors.toList());
		return mapToRoomAvailability(date, startTime, endTime, rooms);
	}

	/**
	 * Method that retrieves the availability for workplaces at the given {@link Location}
	 * at the given date and times.
	 *
	 * @param locationId id of the wanted room
	 * @param date       date
	 * @param startTime  start time
	 * @param endTime    end time
	 * @return a {@link List} of {@link RoomAvailability}.
	 * @see RoomAvailability
	 */
	public List<RoomAvailability> getWorkplaceAvailabilityAtDateTime(Long locationId, LocalDate date,
	                                                                 LocalTime startTime, LocalTime endTime,
																	 Integer amount,
	                                                                 RecurrencePattern recurrencePattern) {
		var rooms = getRoomsWithWorkplacesAvailableAtDateTime(locationId, date, startTime, endTime,
				amount, recurrencePattern);
		return mapToRoomAvailability(date, startTime, endTime, rooms);
	}

	/**
	 * Function that gets the {@link Room rooms} fully available
	 * at a {@link Location} during a timeslot.
	 *
	 * @param locationId id of the {@link Location} of which to get the available rooms.
	 * @param date       date of the timeslot.
	 * @param startTime  start time of the timeslot.
	 * @param endTime    end time of the timeslot.
	 * @return a list of {@link Room rooms} which are fully available during
	 * the entered timeslot.
	 * @see Room
	 * @see Location
	 */
	public List<Room> getRoomsAvailableAtDateTime(Long locationId, LocalDate date,
	                                              LocalTime startTime, LocalTime endTime) {
		checkReservationDateTime(date, startTime, endTime);

		var rooms = findRoomsByLocationId(locationId);

		return rooms
				.stream()
				.filter(room -> isRoomAvailable(room, date, startTime, endTime)).toList();
	}

	/**
	 * Helper method that calculates the available workplaces in the given {@link Room}.
	 *
	 * @param date      date
	 * @param startTime start time
	 * @param endTime   end time
	 * @param room      room
	 * @return the amount of available workplaces in the given  {@link Room}
	 */
	private int calculateAvailable(LocalDate date, LocalTime startTime, LocalTime endTime, Room room) {
		return room.getCapacity() - room.countReservedWorkplaces(date, startTime, endTime);
	}

	/**
	 * Function that gets {@link Room rooms} with at least one workplace
	 * available at the selected {@link Location} during the selected timeslot.
	 *
	 * @param locationId id of the {@link Location} of which to get the available rooms.
	 * @param date       date of the timeslot.
	 * @param startTime  start time of the timeslot.
	 * @param endTime    end time of the timeslot.
	 * @return a list of {@link Room rooms} which have at least one workplace
	 * available during the entered timeslot.
	 * @see Room
	 * @see Location
	 */
	public List<Room> getRoomsWithWorkplacesAvailableAtDateTime(Long locationId, LocalDate date,
	                                                            LocalTime startTime, LocalTime endTime,
																Integer amount, RecurrencePattern recurrencePattern) {
		int finalAmount = amount == null ? 1 : amount;
		checkReservationDateTime(date, startTime, endTime);
		List<Room> allRooms = findRoomsByLocationId(locationId);
		var recurrence = new Recurrence(recurrencePattern != RecurrencePattern.NONE, recurrencePattern);

		return allRooms.stream()
				.filter(room -> (room.countReservedWorkplaces(date, startTime, endTime) + finalAmount <= room.getCapacity())
						&& (room.isWorkplaceRecurrentlyAvailable(new Reservation(date, startTime, endTime,
						null, room, finalAmount, recurrence)))).toList();
	}

	/**
	 * Function that gets determines whether the selected {@link Room} is available
	 * in its entirety during the selected timeslot. It iterates through all the
	 * {@link com.quintor.worqplace.domain.Reservation reservations} of the
	 * {@link Room} and calls the
	 * {@link DateTimeUtils#timeslotsOverlap(LocalDate, LocalTime, LocalTime, Recurrence, LocalDate, LocalTime, LocalTime)
	 * DateTimeUtils.timeslotsOverlap()} function to get whether the new timeslot overlaps
	 * with the old one.
	 *
	 * @param room      the {@link Room} of which to get the availability.
	 * @param date      date of the timeslot.
	 * @param startTime start time of the timeslot.
	 * @param endTime   end time of the timeslot.
	 * @return a boolean determining whether the {@link Room} is fully available.
	 * @see Room
	 * @see Location
	 * @see DateTimeUtils
	 */
	public boolean isRoomAvailable(Room room, LocalDate date, LocalTime startTime, LocalTime endTime) {
		DateTimeUtils.checkReservationDateTime(date, startTime, endTime);
		var activeReservations = room.getReservations().stream()
				.filter(reservation -> reservation.isReservationActive(date)).toList();
		return activeReservations
				.stream()
				.noneMatch(reservation ->
						DateTimeUtils.timeslotsOverlap(
								reservation.getDate(), reservation.getStartTime(),
								reservation.getEndTime(), reservation.getRecurrence(),
								date, startTime, endTime)
				);
	}

	/**
	 * Method that will map the date, start time, end time and rooms to a list of {@link RoomAvailability}
	 *
	 * @param date      date
	 * @param startTime start time
	 * @param endTime   end time
	 * @param rooms     rooms
	 * @return a {@link List} of {@link RoomAvailability}
	 */
	private List<RoomAvailability> mapToRoomAvailability(LocalDate date, LocalTime startTime,
	                                                     LocalTime endTime, List<Room> rooms) {
		return rooms.stream()
				.map(room ->
						new RoomAvailability(
								room.getId(),
								room.getFloor(),
								room.getCapacity(),
								calculateAvailable(date, startTime, endTime, room)
						)
				)
				.collect(Collectors.toList());
	}

	/**
	 * Function that gets the requested {@link Room} by the entered id.
	 *
	 * @param id id of the requested {@link Room}.
	 * @return the requested {@link Room}.
	 * @throws RoomNotFoundException when there is no {@link Room} found in the database
	 *                               with the corresponding id, this exception is thrown.
	 * @see Room
	 * @see RoomRepository
	 * @see RoomNotFoundException
	 */
	public Room findRoomById(Long id) {
		return roomRepository
				.findById(id)
				.orElseThrow(() -> new RoomNotFoundException(id));
	}

	/**
	 * Function that gets all {@link Room rooms} located in the entered {@link Location}.
	 *
	 * @param locationId id of the requested {@link Location}.
	 * @return a list of {@link Room rooms} that are on the given {@link Location}.
	 * @see Room
	 * @see Location
	 * @see LocationService
	 */
	public List<Room> findRoomsByLocationId(Long locationId) {
		var location = locationService.getLocationById(locationId);
		return List.copyOf(location.getRooms());
	}
}
