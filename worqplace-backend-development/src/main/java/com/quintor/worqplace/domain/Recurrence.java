package com.quintor.worqplace.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * The recurrence of the {@link Reservation reservation}, manages if it is
 * active and on what {@link RecurrencePattern pattern} it is set.
 *
 * @see RecurrencePattern
 * @see Reservation
 */
@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Recurrence {
	private boolean active;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RecurrencePattern recurrencePattern;

	/**
	 * Constructor of the {@link Recurrence recurrence} class.
	 *
	 * @param active            whether the reservation is recurring, is false if no
	 *                          recurrence pattern is provided.
	 * @param recurrencePattern the pattern of the recurrence, is null when recurrence is inactive.
	 */
	@JsonCreator
	public Recurrence(boolean active, RecurrencePattern recurrencePattern) {
		if (active && recurrencePattern != null && recurrencePattern != RecurrencePattern.NONE) {
			this.active = true;
			this.recurrencePattern = recurrencePattern;
		} else {
			this.active = false;
			this.recurrencePattern = RecurrencePattern.NONE;
		}
	}
}
