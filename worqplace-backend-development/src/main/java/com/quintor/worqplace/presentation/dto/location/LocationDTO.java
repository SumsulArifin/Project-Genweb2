package com.quintor.worqplace.presentation.dto.location;

import com.quintor.worqplace.domain.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {
	private Long id;
	private String name;
	private Address address;

	// For testing purposes
	@Override
	public String toString() {
		return String.format("{\"id\":%s,\"name\":\"%s\",\"address\":%s}", id, name, address.toTestString());
	}
}
