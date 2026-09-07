package com.klu.hospitality.controller;

import com.klu.hospitality.entity.Room;
import com.klu.hospitality.repository.RoomRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /** Public: list all rooms, optionally filtered to only available ones. */
    @GetMapping
    public List<Room> listRooms(@RequestParam(required = false, defaultValue = "false") boolean availableOnly) {
        return availableOnly ? roomRepository.findByAvailableTrue() : roomRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoom(@PathVariable Long id) {
        return roomRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Admin-only: add a new room to inventory. */
    @PostMapping
    public ResponseEntity<Room> addRoom(@Valid @RequestBody Room room) {
        Room saved = roomRepository.save(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** Admin-only: update price, type, or availability for a room. */
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @Valid @RequestBody Room update) {
        return roomRepository.findById(id)
                .map(room -> {
                    room.setRoomType(update.getRoomType());
                    room.setPricePerNight(update.getPricePerNight());
                    room.setAvailable(update.isAvailable());
                    return ResponseEntity.ok(roomRepository.save(room));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Admin-only: remove a room from inventory. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (!roomRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        roomRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
