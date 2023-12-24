package com.quintor.worqplace.presentation;

import com.quintor.worqplace.application.ReservationService;
import com.quintor.worqplace.application.exceptions.InvalidDayException;
import com.quintor.worqplace.application.exceptions.InvalidStartAndEndTimeException;
import com.quintor.worqplace.application.exceptions.ReservationNotFoundException;
import com.quintor.worqplace.application.exceptions.WorkplacesNotAvailableException;
import com.quintor.worqplace.domain.exceptions.RoomNotAvailableException;
import com.quintor.worqplace.presentation.dto.reservation.AdminReservationDTO;
import com.quintor.worqplace.presentation.dto.reservation.AdminReservationMapper;
import com.quintor.worqplace.presentation.dto.reservation.ReservationDTO;
import com.quintor.worqplace.presentation.dto.reservation.ReservationMapper;
import com.quintor.worqplace.security.data.UserRoles;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.security.RolesAllowed;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Controller for {@link com.quintor.worqplace.domain.Reservation reservations}, contains
 * logic for getting all/specific reservations and reserving
 * {@link com.quintor.worqplace.domain.Room rooms} and workplaces within them.
 *
 * @see ReservationService
 * @see com.quintor.worqplace.domain.Reservation Reservation
 * @see com.quintor.worqplace.domain.Room Room
 */
@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
// methods with a catch clause need generic wildcard return type as it can be the DTO or en error message e.g.
@SuppressWarnings("java:S1452")
public class ReservationController {
	private final ReservationService reservationService;
	private final ReservationMapper reservationMapper;
	private final AdminReservationMapper adminReservationMapper;

	/**
	 * Function that takes the subject from the jwt token.
	 * The subject is the employeeId as set in de {@link com.quintor.worqplace.security.filter.JwtAuthenticationFilter#successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication)}
	 *
	 * @param token JW token with "Bearer " still in front of it.
	 * @return Employee ID of that is embedded in the token.
	 */
	private static long extractIdFromToken(String token) {
		//Strip the "Bearer "
		var bearToken = token.split(" ")[1].split("\\.");

		var decoder = Base64.getDecoder();

		//header would be: jwChunks[0]
		var payload = new String(decoder.decode(bearToken[1]));

		var employeeId = payload.substring(payload.indexOf("sub\":") + 6).split("\"")[0];

		return Long.decode(employeeId);
	}

	/**
	 * Function that retrieves all Authorities (c.q. roles) from the current user.
	 *
	 * @return List of grandted authorities (aka roles).
	 */
	private Collection<? extends GrantedAuthority> getGrantedAuthorities() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
	}

	/**
	 * Function that calls to the {@link ReservationService} to get all
	 * {@link com.quintor.worqplace.domain.Reservation reservations} and then maps
	 * them to a list of
	 * {@link com.quintor.worqplace.presentation.dto.reservation.ReservationDTO
	 * ReservationDTOs}.
	 *
	 * @param includeOld an optional parameter for when one also wants past reservations.
	 * @return a ResponseEntity containing a list of
	 * {@link com.quintor.worqplace.presentation.dto.reservation.ReservationDTO ReservationDTOs}.
	 */
	@GetMapping
	public ResponseEntity<List<ReservationDTO>> getAllReservations(@RequestParam(required = false) boolean includeOld) {
		return new ResponseEntity<>(
				reservationService
						.getAllReservations(includeOld)
						.stream()
						.map(reservationMapper::toReservationDTO)
						.collect(Collectors.toList()),
				HttpStatus.OK
		);
	}

	/**
	 * Function that calls to the {@link ReservationService} to get a
	 * {@link com.quintor.worqplace.domain.Reservation reservation} and then maps
	 * it to a {@link com.quintor.worqplace.presentation.dto.reservation.ReservationDTO
	 * ReservationDTO}.
	 *
	 * @param id Id of the reservation.
	 * @return a ResponseEntity containing a {@link ReservationDTO}.
	 * @see ReservationService
	 * @see com.quintor.worqplace.domain.Reservation Reservation
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getReservationById(@PathVariable long id) {
		try {
			return new ResponseEntity<>(
					reservationMapper.toReservationDTO(reservationService.getReservationById(id)),
					HttpStatus.OK
			);
		} catch (ReservationNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Function that calls to the {@link ReservationService} to create a
	 * {@link com.quintor.worqplace.domain.Reservation Reservation} for a specific
	 * amount of workplaces in the selected {@link com.quintor.worqplace.domain.Room
	 * room} and then maps the return value to a {@link ReservationDTO}.
	 *
	 * @param reservationDTO DTO containing information about the reservation.
	 * @return a ResponseEntity containing a {@link ReservationDTO}.
	 * @see ReservationService
	 * @see com.quintor.worqplace.domain.Reservation Reservation
	 */
	@PostMapping("/workplaces")
	public ResponseEntity<?> reserveWorkplaces(@RequestBody ReservationDTO reservationDTO) {
		try {
			String jwt = getAuthorization();
			reservationDTO.setEmployeeId(extractIdFromToken(jwt));

			return new ResponseEntity<>(
					reservationMapper.toReservationDTO(reservationService.reserveWorkplaces(reservationDTO)),
					HttpStatus.CREATED
			);
		} catch (WorkplacesNotAvailableException | RoomNotAvailableException |
				InvalidStartAndEndTimeException | InvalidDayException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	/**
	 * Function that calls to the {@link ReservationService} to create a
	 * {@link com.quintor.worqplace.domain.Reservation Reservation} for a
	 * {@link com.quintor.worqplace.domain.Room room} and then maps the
	 * return value to a {@link ReservationDTO}.
	 *
	 * @param reservationDTO DTO containing information about the reservation.
	 * @return a ResponseEntity containing a {@link ReservationDTO}.
	 * @see ReservationService
	 * @see com.quintor.worqplace.domain.Reservation Reservation
	 * @see com.quintor.worqplace.domain.Room Room
	 */
	@PostMapping("/rooms")
	public ResponseEntity<?> reserveRoom(@RequestBody ReservationDTO reservationDTO) {
		try {
			String jwt = getAuthorization();
			reservationDTO.setEmployeeId(extractIdFromToken(jwt));

			return new ResponseEntity<>(
					reservationMapper.toReservationDTO(reservationService.reserveRoom(reservationDTO)),
					HttpStatus.CREATED
			);
		} catch (RoomNotAvailableException | WorkplacesNotAvailableException |
				InvalidStartAndEndTimeException | InvalidDayException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	/**
	 * Function that calls to the {@link ReservationService} to get all
	 * {@link com.quintor.worqplace.domain.Reservation reservations} by a user and then
	 * maps them to a list of
	 * {@link com.quintor.worqplace.presentation.dto.reservation.ReservationDTO
	 * ReservationDTOs}.
	 *
	 * @return a ResponseEntity containing a list of
	 * {@link com.quintor.worqplace.presentation.dto.reservation.ReservationDTO
	 * ReservationDTOs}.
	 */
	@GetMapping("/all")
	public ResponseEntity<List<ReservationDTO>> getAllMyReservations(@RequestParam(required = false) Long location,
	                                                                 @RequestParam(required = false)
	                                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			                                                                 LocalDate date) {
		String jwt = getAuthorization();
		var id = extractIdFromToken(jwt);
		return new ResponseEntity<>(
				reservationService
						.getAllMyReservations(id, location, date)
						.stream()
						.map(reservationMapper::toReservationDTO)
						.collect(Collectors.toList()),
				HttpStatus.OK
		);
	}

	/**
	 * Function that retrieves all reservations at the given location.
	 *
	 * @param id locationId
	 * @return List of {@link ReservationDTO}
	 */
	@RolesAllowed("ROLE_ADMIN")
	@GetMapping("/location/{id}")
	public ResponseEntity<List<AdminReservationDTO>> getAllByLocation(@PathVariable long id) {
		return new ResponseEntity<>(
				reservationService
						.getAllByLocation(id)
						.stream()
						.map(adminReservationMapper::toAdminReservationDTO)
						.collect(Collectors.toList()),
				HttpStatus.OK
		);
	}

	/**
	 * Function that deletes a singular reservation by calling to the {@link ReservationService} to delete
	 * {@link com.quintor.worqplace.domain.Reservation reservation} by id
	 *
	 * @return a ResponseEntity containing the id of the deleted
	 * {@link com.quintor.worqplace.domain.Reservation reservation}
	 */
	@PostMapping("/delete/{id}")
	public ResponseEntity<?> deleteById(@PathVariable long id) {
		try {
			var jwt = getAuthorization();
			var employeeId = extractIdFromToken(jwt);

			if (!reservationService.reservationFromEmployee(id, employeeId))
				return new ResponseEntity<>("Reservation was not made by this employee", HttpStatus.FORBIDDEN);

			reservationService.deleteReservation(id);
			return new ResponseEntity<>(id, HttpStatus.OK);
		} catch (ReservationNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Function that updates a reservation if the suggestion for the new reservation is available.
	 * Only lets admins change reservations of other people, otherwise a response will be returned that you cannot
	 * change reservations of others.
	 *
	 * @param reservationDTO {@link ReservationDTO}
	 * @return Whether the update could be performed successfully, if not, a small explanation will be given.
	 */
	@PostMapping("/update")
	public ResponseEntity<?> updateReservation(@RequestBody ReservationDTO reservationDTO) {
		try {
			var jwt = getAuthorization();
			var employeeId = extractIdFromToken(jwt);

			if (!isAdmin() && !reservationService.reservationFromEmployee(reservationDTO.getId(), employeeId))
				return new ResponseEntity<>("You cannot alter the reservation of others.",
						HttpStatus.UNAUTHORIZED);

			return new ResponseEntity<>(
					Stream.of(reservationService.updateReservation(reservationDTO))
							.map(reservationMapper::toReservationDTO),
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		}
	}

	/**
	 * Function that gets the authorization header from the received request.
	 *
	 * @return Bearer token.
	 */
	private String getAuthorization() {
		return ((ServletRequestAttributes) requireNonNull(RequestContextHolder.getRequestAttributes()))
				.getRequest().getHeader("Authorization");
	}

	/**
	 * Function that checks if the current user is an admin.
	 *
	 * @return if the user is an admin.
	 */
	private boolean isAdmin() {
		var adminAuthority = new SimpleGrantedAuthority(UserRoles.ADMIN.getRoleName());
		return getGrantedAuthorities().contains(adminAuthority);
	}
}
