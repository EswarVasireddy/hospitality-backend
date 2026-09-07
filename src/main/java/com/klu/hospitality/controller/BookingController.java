package com.klu.hospitality.controller;

import com.klu.hospitality.entity.Booking;
import com.klu.hospitality.entity.Room;
import com.klu.hospitality.entity.User;
import com.klu.hospitality.repository.BookingRepository;
import com.klu.hospitality.repository.RoomRepository;
import com.klu.hospitality.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public BookingController(BookingRepository bookingRepository, RoomRepository roomRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public record BookingRequest(@NotNull Long roomId, @NotNull LocalDate checkInDate, @NotNull LocalDate checkOutDate) {
    }

    /** The signed-in user's own booking history. */
    @GetMapping("/me")
    public List<Booking> myBookings(Authentication auth) {
        return bookingRepository.findByUser_Username(auth.getName());
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest req, Authentication auth) {
        if (!req.checkOutDate().isAfter(req.checkInDate())) {
            return ResponseEntity.badRequest().body(Map.of("error", "checkOutDate must be after checkInDate"));
        }

        Room room = roomRepository.findById(req.roomId()).orElse(null);
        if (room == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Room not found"));
        }
        if (!room.isAvailable()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Room is not available"));
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + auth.getName()));

        Booking booking = new Booking(room, user, req.checkInDate(), req.checkOutDate());
        room.setAvailable(false);
        roomRepository.save(room);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookingRepository.save(booking));
    }

    /** Cancel a booking and free the room back up. Only the booking's owner may cancel it. */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, Authentication auth) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        if (!booking.getUser().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Not your booking"));
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        Room room = booking.getRoom();
        room.setAvailable(true);
        roomRepository.save(room);

        return ResponseEntity.noContent().build();
    }
}
