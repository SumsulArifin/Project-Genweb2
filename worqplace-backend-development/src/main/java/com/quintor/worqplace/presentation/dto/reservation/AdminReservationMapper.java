package com.quintor.worqplace.presentation.dto.reservation;

import com.quintor.worqplace.domain.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AdminReservationMapper {
	AdminReservationMapper INSTANCE = Mappers.getMapper(AdminReservationMapper.class);

	@Mapping(source = "employee.firstName", target = "employeeFirstName")
	@Mapping(source = "employee.lastName", target = "employeeLastName")
	@Mapping(source = "room.id", target = "roomId")
	AdminReservationDTO toAdminReservationDTO(Reservation reservation);
}

