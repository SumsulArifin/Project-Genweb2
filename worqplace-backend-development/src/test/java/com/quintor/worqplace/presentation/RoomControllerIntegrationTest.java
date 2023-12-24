package com.quintor.worqplace.presentation;

import com.quintor.worqplace.CiTestConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("ci")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(CiTestConfiguration.class)
class RoomControllerIntegrationTest {

	@LocalServerPort
	private int port;
	@Autowired
	private TestRestTemplate restTemplate;

	private String bearer;

	@BeforeEach
	private void init() {
		try {
			Map<String, String> map1 = new HashMap<>();
			map1.put("username", "mdol@quintor.nl");
			map1.put("password", "Kaasje");

			this.bearer = (restTemplate.postForEntity("http://localhost:" + port + "/login", map1, String.class))
					.getHeaders().get("Authorization")
					.toString()
					.replace("[", "")
					.replace("]", "");
		} catch (Exception e) {
			String url = "http://localhost:" + port + "/register";

			Map<String, String> map = new HashMap<>();
			map.put("firstname", "Milan");
			map.put("lastname", "Dol");
			map.put("username", "mdol@quintor.nl");
			map.put("password", "Kaasje");

			restTemplate.postForEntity(url, map, Void.class);

			Map<String, String> map1 = new HashMap<>();
			map1.put("username", "mdol@quintor.nl");
			map1.put("password", "Kaasje");

			this.bearer = (restTemplate.postForEntity("http://localhost:" + port + "/login", map1, String.class))
					.getHeaders().get("Authorization")
					.toString()
					.replace("[", "")
					.replace("]", "");
		}
	}

	@Test
	@DisplayName("Test if a date in far FAR in the future has any rooms available.")
	void listOfAvailableRoomsNotEmpty() {
		String urlPart = "/rooms/availability?";
		urlPart += "locationId=5&";
		urlPart += "date=6000-01-01&";
		urlPart += "start=14:00&";
		urlPart += "end=15:00";

		ResponseEntity<String> result = getRequest(urlPart);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	@DisplayName("listOfAvailableRoomsNotEmpty() should not return an empty list")
	void listOfAvailableRoomsContent() {
		String urlPart = "/rooms/availability?";
		urlPart += "locationId=5&";
		urlPart += "date=6000-01-01&";
		urlPart += "start=14:00&";
		urlPart += "end=15:00";

		ResponseEntity<String> result = getRequest(urlPart);

		assertEquals("[{\"id\":1,\"floor\":3,\"capacity\":24,\"available\":24},{\"id\":2,\"floor\":-1,\"capacity\":6,\"available\":6}]", result.getBody());
	}

	@Test
	@DisplayName("Test if a checking room availability in the past returns an error.")
	void pastRoomsReturnsError() {
		String urlPart = "/rooms/availability?";
		urlPart += "locationId=5&";
		urlPart += "date=2010-01-01&";
		urlPart += "start=14:00&";
		urlPart += "end=15:00";

		ResponseEntity<String> result = getRequest(urlPart);

		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
	}

	@Test
	@DisplayName("Test if a date in far FAR in the future has any workplaces available.")
	void listOfAvailableWorkplacessNotEmpty() {
		String urlPart = "/rooms/availability/workplaces?";
		urlPart += "locationId=5&";
		urlPart += "date=6000-01-01&";
		urlPart += "start=14:00&";
		urlPart += "end=15:00&";
		urlPart += "recurrencePattern=NONE";

		ResponseEntity<String> result = getRequest(urlPart);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	@DisplayName("getWorkplacesAvailability() should not return an empty list")
	void getWorkplacesAvailabilityShouldNotBeEmpty() {
		String urlPart = "/rooms/availability/workplaces?";
		urlPart += "locationId=5&";
		urlPart += "date=6000-01-01&";
		urlPart += "start=14:00&";
		urlPart += "end=15:00&";
		urlPart += "recurrencePattern=NONE";

		ResponseEntity<String> result = getRequest(urlPart);

		assertEquals("[{\"id\":1,\"floor\":3,\"capacity\":24,\"available\":24},{\"id\":2,\"floor\":-1,\"capacity\":6,\"available\":6}]", result.getBody());
	}

	@Test
	@DisplayName("Test if a checking workplace availability in the past returns an error.")
	void pastWorkplacesReturnsError() {
		String urlPart = "/rooms/availability/workplaces?";
		urlPart += "locationId=5&";
		urlPart += "date=2010-01-01&";
		urlPart += "start=14:00&";
		urlPart += "end=15:00&";
		urlPart += "recurrencePattern=NONE";

		ResponseEntity<String> result = getRequest(urlPart);

		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
	}

	/**
	 * Function that uses the {@link TestRestTemplate} to send a GET request
	 * to the Back-End for testing during Continuous Integration.
	 *
	 * @param url the URL Path after "https://localhost:8080".
	 *            as a {@link String}
	 * @return a {@link ResponseEntity} for the request.
	 */
	ResponseEntity<String> getRequest(String url) {
		var request = RequestEntity.get(URI.create(url))
				.header("Authorization", this.bearer)
				.build();

		return restTemplate.exchange(request, String.class);
	}
}
