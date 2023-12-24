package com.quintor.worqplace.presentation;

import com.quintor.worqplace.application.RoomService;
import com.quintor.worqplace.application.exceptions.InvalidDayException;
import com.quintor.worqplace.application.exceptions.InvalidStartAndEndTimeException;
import com.quintor.worqplace.domain.RecurrencePattern;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Controller for {@link com.quintor.worqplace.domain.Room rooms}, contains
 * logic for getting all/specific
 * {@link com.quintor.worqplace.domain.Room rooms} and workplaces within them.
 *
 * @see RoomService
 * @see com.quintor.worqplace.domain.Room Room
 */
@RestController
@RequestMapping("/rooms")
@AllArgsConstructor
// methods with a catch clause need generic wildcard return type as it can be the DTO or en error message e.g.
@SuppressWarnings("java:S1452")
public class RoomController {
	private final RoomService roomService;

	/**
	 * Function that calls to the {@link RoomService} to get the rooms that are fully
	 * available at a given location during a given timeslot.
	 *
	 * @param locationId id of the {@link com.quintor.worqplace.domain.Location Location}.
	 * @param date       date of which to get the availability.
	 * @param startTime  start time on the chosen date.
	 * @param endTime    end time on the chosen date.
	 * @return a ResponseEntity containing a
	 * {@link com.quintor.worqplace.application.util.RoomAvailability}.
	 */
	@GetMapping("/availability")
	public ResponseEntity<?> getRoomsAvailability(
			@RequestParam("locationId") Long locationId,
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
			@RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
			@RequestParam(value = "recurrencePattern", required = false) RecurrencePattern recurrencePattern) {
		try {
			return new ResponseEntity<>(
					roomService.getRoomsAvailabilityAtDateTime(locationId, date, startTime, endTime, recurrencePattern),
					HttpStatus.OK);
		} catch (InvalidDayException | InvalidStartAndEndTimeException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	/**
	 * Function that calls to the {@link RoomService} to get the rooms that have
	 * at least one workplace available during the given timeslot.
	 *
	 * @param locationId id of the {@link com.quintor.worqplace.domain.Location Location}.
	 * @param date       date of which to get the availability.
	 * @param startTime  start time on the chosen date.
	 * @param endTime    end time on the chosen date.
	 * @return a ResponseEntity containing a
	 * {@link com.quintor.worqplace.application.util.RoomAvailability}.
	 */
	@GetMapping("/availability/workplaces")
	public ResponseEntity<?> getWorkplacesAvailability(
			@RequestParam("locationId") Long locationId,
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
			@RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
			@RequestParam(value = "amount", required = false) Integer amount,
			@RequestParam(value = "recurrencePattern", required = false) RecurrencePattern recurrencePattern) {
		try {
			return new ResponseEntity<>(
					roomService.getWorkplaceAvailabilityAtDateTime(locationId, date, startTime, endTime, amount, recurrencePattern),
					HttpStatus.OK
			);
		} catch (InvalidDayException | InvalidStartAndEndTimeException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
}
