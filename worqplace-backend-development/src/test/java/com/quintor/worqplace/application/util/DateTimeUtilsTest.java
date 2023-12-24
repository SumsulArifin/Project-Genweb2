package com.quintor.worqplace.application.util;

import com.quintor.worqplace.application.exceptions.InvalidDayException;
import com.quintor.worqplace.application.exceptions.InvalidStartAndEndTimeException;
import com.quintor.worqplace.domain.Recurrence;
import com.quintor.worqplace.domain.RecurrencePattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.quintor.worqplace.application.util.DateTimeUtils.checkReservationDateTime;
import static com.quintor.worqplace.application.util.DateTimeUtils.timeslotsOverlap;
import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilsTest {

	private static final LocalDate TODAY = LocalDate.now();
	private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
	private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);
	private static final LocalTime NINE = LocalTime.of(9, 0);
	private static final LocalTime TWELVE = LocalTime.of(12, 0);
	private static final LocalTime ONE = LocalTime.of(13, 0);
	private static final LocalTime FOUR = LocalTime.of(16, 0);
	private static final Recurrence NO_RECURRENCE = new Recurrence(false, RecurrencePattern.NONE);
	private static final Recurrence DAILY_RECURRENCE = new Recurrence(true, RecurrencePattern.DAILY);
	private static final Recurrence WEEKLY_RECURRENCE = new Recurrence(true, RecurrencePattern.WEEKLY);
	private static final Recurrence BIWEEKLY_RECURRENCE = new Recurrence(true, RecurrencePattern.BIWEEKLY);
	private static final Recurrence MONTHLY_RECURRENCE = new Recurrence(true, RecurrencePattern.MONTHLY);

	@Test
	@DisplayName("an exception should be thrown if the start time is after the end time.")
	void checkReservationDateTimeShouldThrowIfStartTimeIsAfterBeginTime() {
		assertThrows(InvalidStartAndEndTimeException.class,
				() -> checkReservationDateTime(TODAY, TWELVE, NINE));
	}

	@Test
	@DisplayName("an exception should be thrown if the date is before today")
	void checkReservationDateTimeShouldThrowIfDateIsBeforeToday() {
		assertThrows(InvalidDayException.class,
				() -> checkReservationDateTime(YESTERDAY, NINE, TWELVE));
	}

	@Test
	@DisplayName("no exception should be thrown if the dates and times are okay")
	void checkReservationDateTimeShouldNotThrowIfDateAndTimeAreOkay() {
		assertDoesNotThrow(() -> checkReservationDateTime(TODAY, NINE, TWELVE));
	}

	@Test
	@DisplayName("when there are different input days, the function should return false")
	void timeslotsDontOverlapIfTheyAreOnDifferentDays() {
		assertFalse(timeslotsOverlap(TODAY, NINE, TWELVE, NO_RECURRENCE, TOMORROW, NINE, TWELVE));
	}

	@Test
	@DisplayName("should return when recurring at same date but different time")
	void shouldReturnFalseWhenRecurringAtDifferentTimesDaily() {
		assertFalse(timeslotsOverlap(TODAY, NINE, TWELVE, DAILY_RECURRENCE, TOMORROW, ONE, FOUR));
	}

	@Test
	@DisplayName("should return false when times don't overlap with weekly recurrence")
	void shouldReturnFalseWhenTimesDontOverlapWeeklyRecurrence() {
		var nextWeek = LocalDate.now().plusWeeks(1);
		assertFalse(timeslotsOverlap(TODAY, NINE, TWELVE, WEEKLY_RECURRENCE, nextWeek, ONE, FOUR));
	}

	@Test
	@DisplayName("should return false when times don't overlap with bi weekly recurrence")
	void shouldReturnFalseWhenTimesDontOverlapBiWeeklyRecurrence() {
		var twoWeeks = LocalDate.now().plusWeeks(2);
		assertFalse(timeslotsOverlap(TODAY, NINE, TWELVE, BIWEEKLY_RECURRENCE, twoWeeks, ONE, FOUR));
	}

	@Test
	@DisplayName("should return false when times don't overlap with monthly recurrence")
	void shouldReturnFalseWhenTimesDontOverlapMonthlyRecurrence() {
		var month = LocalDate.now().plusMonths(1);
		assertFalse(timeslotsOverlap(TODAY, NINE, TWELVE, MONTHLY_RECURRENCE, month, ONE, FOUR));
	}

	@Test
	@DisplayName("should return true when times overlap with daily recurrence")
	void shouldReturnTrueWhenTimesOverlapDailyRecurrence() {
		var eleven = LocalTime.of(11, 11);
		var twelve = LocalTime.of(12, 12);

		assertTrue(timeslotsOverlap(TOMORROW, eleven, twelve, DAILY_RECURRENCE, TODAY, eleven, twelve));
	}

	@Test
	@DisplayName("should return true when times overlap with weekly recurrence")
	void shouldReturnTrueWhenTimesOverlapWeeklyRecurrence() {
		var eleven = LocalTime.of(11, 11);
		var twelve = LocalTime.of(12, 12);
		var nextWeek = TOMORROW.plusWeeks(1);

		assertTrue(timeslotsOverlap(TOMORROW, eleven, twelve, WEEKLY_RECURRENCE, nextWeek, eleven, twelve));
	}

	@Test
	@DisplayName("should return true when times overlap with bi weekly recurrence")
	void shouldReturnTrueWhenTimesOverlapBiWeeklyRecurrence() {
		var eleven = LocalTime.of(11, 11);
		var twelve = LocalTime.of(12, 12);
		var nextBiWeek = TOMORROW.plusWeeks(2);

		assertTrue(timeslotsOverlap(TOMORROW, eleven, twelve, BIWEEKLY_RECURRENCE, nextBiWeek, eleven, twelve));
	}

	@Test
	@DisplayName("should return true when times overlap with monthly recurrence")
	void shouldReturnTrueWhenTimesOverlapMonthlyRecurrence() {
		var eleven = LocalTime.of(11, 11);
		var twelve = LocalTime.of(12, 12);
		var nextMonth = TOMORROW.plusMonths(1);

		assertTrue(timeslotsOverlap(TOMORROW, eleven, twelve, MONTHLY_RECURRENCE, nextMonth, eleven, twelve));
	}

	@Test
	@DisplayName("should return false when both dates equals with weekly recurrence")
	void shouldReturnFalseWhenSameDatesWithWeeklyRecurrence() {
		assertFalse(timeslotsOverlap(TODAY, NINE, TWELVE, WEEKLY_RECURRENCE, TODAY, ONE, FOUR));
	}

	@Test
	@DisplayName("should return false when both dates equals with bi weekly recurrence")
	void shouldReturnFalseWhenSameDatesWithBiWeeklyRecurrence() {
		assertFalse(timeslotsOverlap(TODAY, NINE, TWELVE, BIWEEKLY_RECURRENCE, TODAY, ONE, FOUR));
	}

	@Test
	@DisplayName("should return false when both dates equals with monthly recurrence")
	void shouldReturnFalseWhenSameDatesWithMontylyRecurrence() {
		assertFalse(timeslotsOverlap(TODAY, NINE, TWELVE, MONTHLY_RECURRENCE, TODAY, ONE, FOUR));
	}

	@Test
	@DisplayName("should return false when new end time is before existing start time")
	void shouldReturnFalseWhenNewEndTimeIsBeforeExistingStartTime() {
		var seven = LocalTime.of(7, 0);
		var eight = LocalTime.of(8, 0);
		assertFalse(timeslotsOverlap(TODAY, NINE, TWELVE, MONTHLY_RECURRENCE, TODAY, seven, eight));
	}

	@Test
	@DisplayName("should return false when dates don't equal with weekly recurrence")
	void shouldReturnFalseWhenDifferentDatesWithWeeklyRecurrence() {
		assertFalse(timeslotsOverlap(TOMORROW, ONE, FOUR, WEEKLY_RECURRENCE, TODAY, ONE, FOUR));
	}

	@Test
	@DisplayName("should return false when dates don't equal with bi weekly recurrence")
	void shouldReturnFalseWhenDifferentDatesWithBiWeeklyRecurrence() {
		assertFalse(timeslotsOverlap(TOMORROW, ONE, FOUR, BIWEEKLY_RECURRENCE, TODAY, ONE, FOUR));
	}

	@Test
	@DisplayName("should return false when dates don't equal with monthly recurrence")
	void shouldReturnFalseWhenDifferentDatesWithMonthlyRecurrence() {
		assertFalse(timeslotsOverlap(TOMORROW, ONE, FOUR, MONTHLY_RECURRENCE, TODAY, ONE, FOUR));
	}
}
