package com.klu.hospitality.repository;

import com.klu.hospitality.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByAvailableTrue();
    List<Room> findByRoomType(String roomType);
}
