package com.quintor.worqplace.presentation;

import com.quintor.worqplace.CiTestConfiguration;
import com.quintor.worqplace.domain.Address;
import com.quintor.worqplace.domain.Location;
import com.quintor.worqplace.presentation.dto.location.LocationDTO;
import com.quintor.worqplace.presentation.dto.location.LocationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("ci")
@Import(CiTestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocationControllerIntegrationTest {

	private String bearer;
	@LocalServerPort
	private int port;
	@Autowired
	private TestRestTemplate restTemplate;
	private Location amersfoort;
	private Location denBosch;
	private Location deventer;
	private Location denHaag;
	private Location groningen;

	@BeforeEach
	void initialize() {
		this.amersfoort = new Location(1L, "Quintor Amersfoort", new Address(1L, 14, "m", "Maanlander", "3824 MP", "Amersfoort"), List.of());
		this.denBosch = new Location(2L, "Quintor Den Bosch", new Address(2L, 1, "", "Havensingel", "5211 TX", "Den Bosch"), List.of());
		this.deventer = new Location(3L, "Quintor Deventer", new Address(3L, 6, "", "Zutphenseweg", "7418 AJ", "Deventer"), List.of());
		this.denHaag = new Location(4L, "Quintor Den Haag", new Address(4L, 4, "-5", "Lange Vijverberg", "2513 AC", "Den Haag"), List.of());
		this.groningen = new Location(5L, "Quintor Groningen", new Address(5L, 112, "", "Ubbo Emmiussingel", "9711 BK", "Groningen"), List.of());

		setupBearerToken();
	}

	@Test
	@DisplayName("GetLocations() should return all locations")
	void getLocationShouldReturnLocations() {
		ResponseEntity<List<LocationDTO>> responseEntity =
				new ResponseEntity<>(Stream.of(amersfoort, denBosch, deventer, denHaag, groningen)
						.map(LocationMapper.INSTANCE::toLocationDTO)
						.collect(Collectors.toList()), HttpStatus.OK);

		String formattedResponse = Objects.requireNonNull(responseEntity.getBody())
				.toString()
				.replaceAll(",\\s", ",");

		var request = RequestEntity.get(URI.create("http://localhost:" + port + "/locations"))
				.header("Authorization", this.bearer)
				.build();

		assertEquals(formattedResponse, this.restTemplate.exchange(request, String.class).getBody());
	}

	private void setupBearerToken() {
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
}
