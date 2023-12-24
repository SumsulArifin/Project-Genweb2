package com.quintor.worqplace.data;

import com.quintor.worqplace.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class that connects the {@link Room}
 * application to the database to store the domain.
 *
 * @see Room
 * @see com.quintor.worqplace.application.RoomService RoomService
 */
public interface RoomRepository extends JpaRepository<Room, Long> {

}
