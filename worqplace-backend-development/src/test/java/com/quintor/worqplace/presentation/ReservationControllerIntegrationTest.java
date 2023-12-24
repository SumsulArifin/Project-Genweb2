package com.quintor.worqplace.presentation;

import com.quintor.worqplace.CiTestConfiguration;
import com.quintor.worqplace.data.LocationRepository;
import com.quintor.worqplace.data.ReservationRepository;
import com.quintor.worqplace.domain.*;
import com.quintor.worqplace.presentation.dto.reservation.ReservationDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("ci")
@Import(CiTestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReservationControllerIntegrationTest {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private LocationRepository locationRepository;

	private Employee employee;

	private Room room;
	private Room room1;

	private Address address;
	private Address address1;

	private Location location;

	private Recurrence monthlyRecurrence;
	private Recurrence weeklyRecurrence1;

	private Reservation reservation;
	private Reservation reservation1;
	private Reservation reservation3;

	private String bearer;

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

	@BeforeAll
	void initialize() {
//		Employee
		this.employee = new Employee(1L, "Quinten", "Tor");

//		Address
		this.address = new Address(1L, 1, "", "Torro", "4369GH", "Torr");
		this.address1 = new Address(2L, 12, "A", "Zuidel straat", "1249LJ", "Quintara");

//		Location
		this.location = new Location(6L, "Quintor - Test", address, null);

//		Room
		this.room = new Room(1L, 1, location, 5, null);
		this.room1 = new Room(2L, 2, location, 8, null);

//		Recurrence
		this.monthlyRecurrence = new Recurrence(true, RecurrencePattern.MONTHLY);
		this.weeklyRecurrence1 = new Recurrence(false, RecurrencePattern.WEEKLY);

//		Workplace
		this.reservation = new Reservation(1L, LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(19, 0), employee, room, 1, monthlyRecurrence);
		this.reservation1 = new Reservation(2L, LocalDate.now().plusWeeks(1), LocalTime.of(9, 0), LocalTime.of(19, 0), employee, room, 2, monthlyRecurrence);

//		Room
		this.reservation3 = new Reservation(4L, LocalDate.now().plusWeeks(2), LocalTime.of(7, 3), LocalTime.of(8, 7), employee, room1, 8, weeklyRecurrence1);

		setupBearerToken();
	}

	@AfterEach
	void tearDown() {
		reservationRepository.deleteAll();
	}

	@Test
	@DisplayName("getAllReservations() should return 200 OK")
	void shouldReturn200() {
		ResponseEntity<String> result = getRequest("/reservations");
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	@DisplayName("getAllReservations() should return all reservations")
	void shouldReturnAllReservations() {
		reservationRepository.save(reservation1);
		reservationRepository.save(reservation);

		ResponseEntity<String> result = getRequest("/reservations");

		assertTrue(requireNonNull(result.getBody()).contains("\"startTime\":\"09:00:00\",\"endTime\":\"19:00:00\",\"employeeId\":%s,\"roomId\":1,\"workplaceAmount\":1,\"recurrence\":{\"active\":true,\"recurrencePattern\":\"MONTHLY\"}".formatted(employee.getId())) &&
				requireNonNull(result.getBody()).contains("\"startTime\":\"09:00:00\",\"endTime\":\"19:00:00\",\"employeeId\":%s,\"roomId\":1,\"workplaceAmount\":2,\"recurrence\":{\"active\":true,\"recurrencePattern\":\"MONTHLY\"}".formatted(employee.getId())));
	}

	@Test
	@DisplayName("getAllReservations should return an empty list of reservations if there are none")
	void shouldReturnEmptyListIfNoReservations() {
		ResponseEntity<String> result = getRequest("/reservations");

		assertEquals(Collections.emptyList().toString(), result.getBody());
	}

	@Test
	@DisplayName("getReservationById() should return reservation 200 OK there is one")
	void getReservationByIdShouldReturn200() {
		reservationRepository.save(reservation);
		ResponseEntity<String> result = getRequest("/reservations/" + reservation.getId());

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	@DisplayName("getReservationById() should return reservation if there is one")
	void getReservationByIdShouldReturnReservation() {
		reservationRepository.save(reservation);
		ResponseEntity<String> result = getRequest("/reservations");

		assertTrue(
				requireNonNull(result.getBody()).contains("\"startTime\":\"09:00:00\",\"endTime\":\"19:00:00\",\"employeeId\":%s,\"roomId\":1,\"workplaceAmount\":1,\"recurrence\":{\"active\":true,\"recurrencePattern\":\"MONTHLY\"}".formatted(employee.getId()))
		);
	}

	@Test
	@DisplayName("getReservationById() should return 404 when not found")
	void getReservationByIdShouldReturn404() {
		ResponseEntity<String> result = getRequest("/reservations/99");

		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
	}

	@Test
	@DisplayName("reserveWorkplaces() should return 201 upon reservation")
	void reserveWorkplacesShouldReturn201() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setDate(LocalDate.now().plusDays(1));
		reservationDTO.setStartTime(LocalTime.of(9, 0));
		reservationDTO.setEndTime(LocalTime.of(19, 0));
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(room.getId());
		reservationDTO.setWorkplaceAmount(4);
		reservationDTO.setRecurrence(weeklyRecurrence1);

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);
		var request = new HttpEntity<>(reservationDTO, headers);

		assertEquals(HttpStatus.CREATED, this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/workplaces", port), request, String.class).getStatusCode());
	}

	@Test
	@DisplayName("reserveWorkplaces() should return reservation info if reservation went successful")
	void reserveWorkplacesShouldReturnReservationInfo() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setDate(LocalDate.now().plusDays(1));
		reservationDTO.setStartTime(LocalTime.of(9, 0));
		reservationDTO.setEndTime(LocalTime.of(19, 0));
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(room.getId());
		reservationDTO.setWorkplaceAmount(1);
		reservationDTO.setRecurrence(weeklyRecurrence1);

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);
		var request = new HttpEntity<>(reservationDTO, headers);

		String result = this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/workplaces", port), request, String.class).getBody();

		assertTrue(requireNonNull(result).contains(String.format(",\"date\":\"%s\",\"startTime\":\"09:00:00\",\"endTime\":\"19:00:00\",\"employeeId\":%s,\"roomId\":1,\"workplaceAmount\":1,\"recurrence\":{\"active\":false,\"recurrencePattern\":\"NONE\"}}", reservationDTO.getDate(), employee.getId())));
	}

	@Test
	@DisplayName("reserveWorkplaces() should return 422 if workplace is not available")
	void reserveWorkplaceShouldReturn422IfNotAvailable() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setDate(LocalDate.now().plusDays(1));
		reservationDTO.setStartTime(LocalTime.of(9, 0));
		reservationDTO.setEndTime(LocalTime.of(19, 0));
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(room.getId());
		reservationDTO.setWorkplaceAmount(13);
		reservationDTO.setRecurrence(weeklyRecurrence1);

		reservationRepository.save(reservation3);

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);
		var request = new HttpEntity<>(reservationDTO, headers);

		String result = this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/workplaces", port), request, String.class).getBody();

		assertTrue(requireNonNull(result).contains(String.format(",\"date\":\"%s\",\"startTime\":\"09:00:00\",\"endTime\":\"19:00:00\",\"employeeId\":%s,\"roomId\":1,\"workplaceAmount\":13,\"recurrence\":{\"active\":false,\"recurrencePattern\":\"NONE\"}}", reservationDTO.getDate(), employee.getId())));
	}

	@Test
	@DisplayName("reserveWorkplaces() should return 422 if times are invalid")
	void reserveWorkplaceShouldReturn422IfTimesAreInvalid() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setDate(LocalDate.now().plusDays(1));
		reservationDTO.setStartTime(LocalTime.of(9, 0));
		reservationDTO.setEndTime(LocalTime.of(8, 57));
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(room.getId());
		reservationDTO.setWorkplaceAmount(4);
		reservationDTO.setRecurrence(weeklyRecurrence1);

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);
		var request = new HttpEntity<>(reservationDTO, headers);

		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/workplaces", port), request, String.class).getStatusCode());
	}

	@Test
	@DisplayName("reserveWorkplaces() should return 422 if date is invalid")
	void reserveWorkplaceShouldReturn422IfDateIsInvalid() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setDate(LocalDate.now().minusDays(1));
		reservationDTO.setStartTime(LocalTime.of(9, 0));
		reservationDTO.setEndTime(LocalTime.of(19, 0));
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(room.getId());
		reservationDTO.setWorkplaceAmount(1);
		reservationDTO.setRecurrence(weeklyRecurrence1);

		reservationRepository.save(reservation3);

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);
		var request = new HttpEntity<>(reservationDTO, headers);

		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/workplaces", port), request, String.class).getStatusCode());
	}

	@Test
	@DisplayName("reserveRoom() should return 201 upon reservation")
	void reserveRoomShouldReturn201() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setDate(LocalDate.now().plusDays(1));
		reservationDTO.setStartTime(LocalTime.of(9, 0));
		reservationDTO.setEndTime(LocalTime.of(19, 0));
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(room.getId());
		reservationDTO.setRecurrence(weeklyRecurrence1);

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);
		var request = new HttpEntity<>(reservationDTO, headers);

		assertEquals(HttpStatus.CREATED, this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/rooms", port), request, String.class).getStatusCode());
	}

	@Test
	@DisplayName("reserveRoom() should return reservation info if reservation went successful")
	void reserveRoomShouldReturnReservationInfo() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setDate(LocalDate.now().plusDays(1));
		reservationDTO.setStartTime(LocalTime.of(9, 0));
		reservationDTO.setEndTime(LocalTime.of(8, 59));
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(room.getId());
		reservationDTO.setWorkplaceAmount(1);
		reservationDTO.setRecurrence(monthlyRecurrence);

		assertNotNull(this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/rooms", port), reservationDTO, String.class).getBody());
	}

	@Test
	@DisplayName("reserveRoom() should return 422 if room is not available")
	void reserveRoomeShouldReturn422IfNotAvailable() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setDate(LocalDate.now().plusDays(1));
		reservationDTO.setStartTime(LocalTime.of(9, 0));
		reservationDTO.setEndTime(LocalTime.of(8, 59));
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(room.getId());
		reservationDTO.setWorkplaceAmount(1);
		reservationDTO.setRecurrence(monthlyRecurrence);

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);
		var request = new HttpEntity<>(reservationDTO, headers);

		this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/rooms", port), request, String.class);
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/rooms", port), request, String.class).getStatusCode());
	}

	@Test
	@DisplayName("reserveRoom() should return 422 if times are invalid")
	void reserveRoomShouldReturn422IfTimesAreInvalid() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setDate(LocalDate.now().plusDays(1));
		reservationDTO.setStartTime(LocalTime.of(9, 0));
		reservationDTO.setEndTime(LocalTime.of(8, 59));
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(room.getId());
		reservationDTO.setWorkplaceAmount(1);
		reservationDTO.setRecurrence(monthlyRecurrence);

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);
		var request = new HttpEntity<>(reservationDTO, headers);

		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/rooms", port), request, String.class).getStatusCode());
	}

	@Test
	@DisplayName("reserveRoom() should return 422 if date is invalid")
	void reserveRoomShouldReturn422IfDateIsInvalid() {
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setDate(LocalDate.now().minusDays(1));
		reservationDTO.setStartTime(LocalTime.of(9, 0));
		reservationDTO.setEndTime(LocalTime.of(19, 0));
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(room.getId());
		reservationDTO.setWorkplaceAmount(1);
		reservationDTO.setRecurrence(monthlyRecurrence);

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);
		var request = new HttpEntity<>(reservationDTO, headers);

		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/rooms", port), request, String.class, headers).getStatusCode());
	}

	@Test
	@DisplayName("getAllMyReservations() should return 200 OK")
	void getAllMyReservationsShouldReturn200() {
		var result = getRequest("/reservations/all");

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	@DisplayName("getAllMyReservations() should return reservations if there are any")
	void getAllMyReservationsShouldReturnListOfReservations() {
		reservationRepository.save(reservation);
		reservationRepository.save(reservation1);

		var result = getRequest("/reservations/all");

		assertTrue(result.getBody().contains(String.format("\"date\":\"%s\",\"startTime\":\"09:00:00\",\"endTime\":\"19:00:00\",\"employeeId\":%s,\"roomId\":1,\"workplaceAmount\":1,\"recurrence\":{\"active\":true,\"recurrencePattern\":\"MONTHLY\"}},", reservation.getDate(), employee.getId())) &&
				result.getBody().contains(String.format("\"date\":\"%s\",\"startTime\":\"09:00:00\",\"endTime\":\"19:00:00\",\"employeeId\":%s,\"roomId\":1,\"workplaceAmount\":2,\"recurrence\":{\"active\":true,\"recurrencePattern\":\"MONTHLY\"}}]", reservation1.getDate(), employee.getId())));
	}

	@Test
	@DisplayName("getAllMyReservations() should return empty list if there are none")
	void getAllMyReservationsShouldReturnEmptyList() {
		var result = getRequest("/reservations/all");

		assertEquals(Collections.emptyList().toString(), result.getBody());
	}

	@Test
	@DisplayName("deleteReservation() should return 200 OK")
	void deleteOwnReservation() {
		reservationRepository.save(reservation);
		var result = getRequest("/reservations/");
		Matcher m = Pattern.compile("id\":(.*?),").matcher(result.getBody());
		boolean found = m.find();

		assertTrue(found);

		result = postRequest("/reservations/delete/" + m.group(1));

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	@DisplayName("deleteReservation() should return 403 when not own")
	void deleteOtherReservation() {
		reservationRepository.save(reservation3);
		register("Test", "Je", "test@quintor.nl");
		login("test@quintor.nl");
		var result = getRequest("/reservations/");
		Matcher m = Pattern.compile("id\":(.*?),").matcher(result.getBody());
		boolean found = m.find();

		assertTrue(found);

		result = postRequest("/reservations/delete/" + m.group(1));

		assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
	}

	@Test
	@DisplayName("getAllByLocation should all reservations by location")
	void getAllByLocationShouldReturnAllReservationsByLocation() {
		locationRepository.save(location);
		reservationRepository.save(reservation);
		reservationRepository.save(reservation1);
		reservationRepository.save(reservation3);

		login("admin@quintor.nl");

		var result = getRequest("/reservations/location/5");
		System.out.println(result.getBody());
		assertTrue(result.getBody().contains("\"startTime\":\"09:00:00\",\"endTime\":\"19:00:00\",\"employeeFirstName\":\"Milan\",\"employeeLastName\":\"Dol\",\"roomId\":1,\"workplaceAmount\":1,\"recurrence\":{\"active\":true,\"recurrencePattern\":\"MONTHLY\"") &&
				result.getBody().contains("\"startTime\":\"09:00:00\",\"endTime\":\"19:00:00\",\"employeeFirstName\":\"Milan\",\"employeeLastName\":\"Dol\",\"roomId\":1,\"workplaceAmount\":2,\"recurrence\":{\"active\":true,\"recurrencePattern\":\"MONTHLY\"") &&
				result.getBody().contains("\"startTime\":\"07:03:00\",\"endTime\":\"08:07:00\",\"employeeFirstName\":\"Milan\",\"employeeLastName\":\"Dol\",\"roomId\":2,\"workplaceAmount\":8,\"recurrence\":{\"active\":false,\"recurrencePattern\":\"NONE\""));
	}

	@Test
	@DisplayName("updateReservation should update if admin updates someone else's reservation")
	void updateReservationShouldUpdateIfAdminUpdatesReservationOfOtherPerson() {
		login();
		reservation.setEmployee(employee);
		var savedReservation = reservationRepository.save(reservation);
		login("admin@quintor.nl");

		var reservationDTO = new ReservationDTO();
		reservationDTO.setId(savedReservation.getId());
		reservationDTO.setDate(reservation.getDate().plusDays(1));
		reservationDTO.setStartTime(reservation.getStartTime());
		reservationDTO.setEndTime(reservation.getEndTime());
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(reservation.getId());
		reservationDTO.setWorkplaceAmount(reservation.getWorkplaceAmount());
		reservationDTO.setRecurrence(reservation.getRecurrence());

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);

		var request = new HttpEntity<>(reservationDTO, headers);

		var result = this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/update", port), request, String.class);

		System.out.println(result.getBody());

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	@DisplayName("updateReservation should update if data is correct")
	void updateReservationShouldUpdateIfDataIsCorrect() {
		login();
		reservation.setEmployee(employee);
		var savedReservation = reservationRepository.save(reservation);

		var reservationDTO = new ReservationDTO();
		reservationDTO.setId(savedReservation.getId());
		reservationDTO.setDate(reservation.getDate().plusDays(1));
		reservationDTO.setStartTime(reservation.getStartTime());
		reservationDTO.setEndTime(reservation.getEndTime());
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(reservation.getId());
		reservationDTO.setWorkplaceAmount(reservation.getWorkplaceAmount());
		reservationDTO.setRecurrence(reservation.getRecurrence());

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);

		var request = new HttpEntity<>(reservationDTO, headers);

		var result = this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/update", port), request, String.class);
		System.out.println(result.getBody());

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	@DisplayName("updateReservation should return UNAUTHORIZED if it is not their reservation")
	void updateReservationShouldReturnUnauthorizedIfItIsNotTheirReservation() {
		login("admin@quintor.nl");
		reservation.setEmployee(employee);
		var savedReservation = reservationRepository.save(reservation);

		var reservationDTO = new ReservationDTO();
		reservationDTO.setId(savedReservation.getId());
		reservationDTO.setDate(reservation.getDate().plusDays(1));
		reservationDTO.setStartTime(reservation.getStartTime());
		reservationDTO.setEndTime(reservation.getEndTime());
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(reservation.getId());
		reservationDTO.setWorkplaceAmount(reservation.getWorkplaceAmount());
		reservationDTO.setRecurrence(reservation.getRecurrence());

		login();

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);

		var request = new HttpEntity<>(reservationDTO, headers);

		var result = this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/update", port), request, String.class);

		System.out.println(result.getBody());
		assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
	}

	@Test
	@DisplayName("updateReservation should return conflict if the update is not possible")
	void updateReservationShouldReturnConflictIfTheUpdateIsNotPossible() {
		login("admin@quintor.nl");
		reservation.setEmployee(employee);
		reservationRepository.save(reservation);
		login();

		var reservationDTO = new ReservationDTO();
		reservationDTO.setId(reservation.getId());
		reservationDTO.setDate(reservation.getDate().plusDays(1));
		reservationDTO.setStartTime(reservation.getStartTime());
		reservationDTO.setEndTime(reservation.getEndTime());
		reservationDTO.setEmployeeId(employee.getId());
		reservationDTO.setRoomId(reservation.getId());
		reservationDTO.setWorkplaceAmount(reservation.getWorkplaceAmount());
		reservationDTO.setRecurrence(reservation.getRecurrence());

		var headers = new HttpHeaders();
		headers.set("Authorization", this.bearer);

		var request = new HttpEntity<>(reservationDTO, headers);

		var result = this.restTemplate.postForEntity(String.format("http://localhost:%s/reservations/update", port), request, String.class);

		assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
	}


	/**
	 * Function that uses the {@link TestRestTemplate} to send a GET request
	 * to the Back-End for testing during Continuous Integration.
	 *
	 * @param url the URL Path after "https://localhost:{port}".
	 *            as a {@link String}
	 * @return a {@link ResponseEntity} for the request.
	 */
	ResponseEntity<String> getRequest(String url) {
		var request = RequestEntity.get(URI.create(url))
				.header("Authorization", this.bearer)
				.build();

		return restTemplate.exchange(request, String.class);
	}

	/**
	 * Function that uses the {@link TestRestTemplate} to send a POST request
	 * to the Back-End for testing during Continuous Integration.
	 *
	 * @param url the URL Path after "https://localhost:8080".
	 *            as a {@link String}
	 * @return a {@link ResponseEntity} for the request.
	 */
	ResponseEntity<String> postRequest(String url) {
		var request = RequestEntity.post(URI.create(url))
				.header("Authorization", this.bearer)
				.build();

		return restTemplate.exchange(request, String.class);
	}

	/**
	 * Function that uses the {@link TestRestTemplate} to send a DELETE request
	 * to the Back-End for testing during Continuous Integration.
	 *
	 * @param url the URL Path after "https://localhost:8080".
	 *            as a {@link String}
	 * @return a {@link ResponseEntity} for the request.
	 */
	ResponseEntity<String> deleteRequest(String url) {
		var request = RequestEntity.delete(URI.create(url))
				.header("Authorization", this.bearer)
				.build();

		return restTemplate.exchange(request, String.class);
	}

	private void setupBearerToken() {
		try {
			login();
		} catch (Exception e) {
			register();
			login();
		}
	}

	private void register() {
		register("Milan", "Dol", "mdol@quintor.nl");
	}

	private void register(String firstname, String lastname, String username) {
		String url = "http://localhost:" + port + "/register";

		Map<String, String> map = new HashMap<>();
		map.put("firstname", firstname);
		map.put("lastname", lastname);
		map.put("username", username);
		map.put("password", "Kaasje");

		restTemplate.postForEntity(url, map, Void.class);
	}

	private void login() {
		login("mdol@quintor.nl");
	}

	private void login(String username) {
		Map<String, String> map1 = new HashMap<>();
		map1.put("username", username);
		map1.put("password", "Kaasje");

		this.bearer = (restTemplate.postForEntity("http://localhost:" + port + "/login", map1, String.class))
				.getHeaders().get("Authorization")
				.toString()
				.replace("[", "")
				.replace("]", "");

		this.employee.setId(extractIdFromToken(this.bearer));
	}
}
