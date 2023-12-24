package com.quintor.worqplace.presentation.dto.reservation;

import com.quintor.worqplace.domain.Recurrence;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReservationDTO {
	private Long id;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
	@Nullable
	private Long employeeId;
	private Long roomId;
	private int workplaceAmount;
	private Recurrence recurrence;
}
