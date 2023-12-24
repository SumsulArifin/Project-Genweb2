package com.quintor.worqplace.application.util;

/**
 * Datatransfer / holder object used by {@link com.quintor.worqplace.application.RoomService} to transfer
 * this data to the {@link com.quintor.worqplace.presentation.RoomController}
 */
public record RoomAvailability(Long id, int floor, int capacity, int available) {
}
