package com.voting.system.repository;

import com.voting.system.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    Optional<Room> findByRoomId(String roomId);
    boolean existsByRoomId(String roomId);
}
