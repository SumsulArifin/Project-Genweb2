package com.quintor.worqplace.presentation.dto.reservation;

import com.quintor.worqplace.domain.Recurrence;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AdminReservationDTO {
	private Long id;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
	private String employeeFirstName;
	private String employeeLastName;
	private Long roomId;
	private int workplaceAmount;
	private Recurrence recurrence;
}
