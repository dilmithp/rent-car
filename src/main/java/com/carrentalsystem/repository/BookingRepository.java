package com.carrentalsystem.repository;

import com.carrentalsystem.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // Find booking by ID with eager fetch
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.vehicle WHERE b.bookingId = :id")
    Optional<Booking> findByIdWithDetails(@Param("id") Integer id);

    // Override findAll to eagerly fetch relationships
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.vehicle")
    List<Booking> findAll();

    // Find by customer ID
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.vehicle WHERE b.customer.customerId = :customerId")
    List<Booking> findByCustomer_CustomerId(@Param("customerId") Integer customerId);

    // Find by vehicle ID
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.vehicle WHERE b.vehicle.vehicleId = :vehicleId")
    List<Booking> findByVehicle_VehicleId(@Param("vehicleId") Integer vehicleId);

    // Find by booking status
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.vehicle WHERE b.bookingStatus = :status")
    List<Booking> findByBookingStatus(@Param("status") String status);

    // Find by payment status
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.vehicle WHERE b.paymentStatus = :status")
    List<Booking> findByPaymentStatus(@Param("status") String status);

    // Find by customer and status
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.vehicle WHERE b.customer.customerId = :customerId AND b.bookingStatus = :status")
    List<Booking> findByCustomer_CustomerIdAndBookingStatus(@Param("customerId") Integer customerId, @Param("status") String status);

    // Count bookings by status
    long countByBookingStatus(String status);

    // Count bookings by payment status
    long countByPaymentStatus(String status);

    // Find bookings between dates
    @Query("SELECT b FROM Booking b WHERE b.startDate >= :startDate AND b.endDate <= :endDate ORDER BY b.startDate DESC")
    List<Booking> findBookingsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find active bookings for a vehicle
    @Query("SELECT b FROM Booking b WHERE b.vehicle.vehicleId = :vehicleId " +
           "AND b.bookingStatus IN ('Confirmed', 'Active') " +
           "AND b.endDate >= CURRENT_DATE")
    List<Booking> findActiveBookingsForVehicle(@Param("vehicleId") Integer vehicleId);

    // Find pending payment bookings
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.vehicle WHERE b.paymentStatus = 'Pending' ORDER BY b.bookingDate DESC")
    List<Booking> findPendingPaymentBookings();

    // Get recent bookings
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.vehicle ORDER BY b.createdAt DESC")
    List<Booking> findRecentBookings();

    // Check if vehicle is available for booking period
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN false ELSE true END FROM Booking b " +
           "WHERE b.vehicle.vehicleId = :vehicleId " +
           "AND b.bookingStatus IN ('Confirmed', 'Active') " +
           "AND ((b.startDate <= :endDate AND b.endDate >= :startDate))")
    boolean isVehicleAvailable(@Param("vehicleId") Integer vehicleId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
}

