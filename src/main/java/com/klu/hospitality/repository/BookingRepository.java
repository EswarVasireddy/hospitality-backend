package com.klu.hospitality.repository;

import com.klu.hospitality.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Username(String username);
    List<Booking> findByRoom_Id(Long roomId);
}
