package com.quintor.worqplace.presentation.dto.reservation;

import com.quintor.worqplace.domain.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
	ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

	@Mapping(source = "employee.id", target = "employeeId")
	@Mapping(source = "room.id", target = "roomId")
	ReservationDTO toReservationDTO(Reservation reservation);
}
