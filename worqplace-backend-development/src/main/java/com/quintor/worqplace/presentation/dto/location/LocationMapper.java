package com.quintor.worqplace.presentation.dto.location;

import com.quintor.worqplace.domain.Location;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LocationMapper {
	LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

	LocationDTO toLocationDTO(Location location);
}
