package com.carrentalsystem.service;

import com.carrentalsystem.models.Booking;
import com.carrentalsystem.models.Customer;
import com.carrentalsystem.models.Vehicle;
import com.carrentalsystem.repository.BookingRepository;
import com.carrentalsystem.repository.CustomerRepository;
import com.carrentalsystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

    private static final String UPLOAD_DIR = "uploads/payment_slips/";

    @Transactional
    public Booking createBooking(Booking booking) {
        // Validate vehicle availability
        if (!bookingRepository.isVehicleAvailable(
                booking.getVehicle().getVehicleId(),
                booking.getStartDate(),
                booking.getEndDate())) {
            throw new RuntimeException("Vehicle is not available for the selected dates");
        }

        // Calculate total days and amount
        long days = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());
        if (days <= 0) {
            throw new RuntimeException("End date must be after start date");
        }

        booking.setTotalDays((int) days);
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicle().getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        BigDecimal totalAmount = vehicle.getDailyRate().multiply(BigDecimal.valueOf(days));
        booking.setTotalAmount(totalAmount);
        booking.setBookingDate(LocalDate.now());
        booking.setBookingStatus("Pending");
        booking.setPaymentStatus("Pending");

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking updateBooking(Booking booking) {
        Optional<Booking> existingBooking = bookingRepository.findById(booking.getBookingId());
        if (existingBooking.isPresent()) {
            Booking bookingToUpdate = existingBooking.get();
            bookingToUpdate.setStartDate(booking.getStartDate());
            bookingToUpdate.setEndDate(booking.getEndDate());
            bookingToUpdate.setTotalDays(booking.getTotalDays());
            bookingToUpdate.setTotalAmount(booking.getTotalAmount());
            bookingToUpdate.setBookingStatus(booking.getBookingStatus());
            bookingToUpdate.setPaymentStatus(booking.getPaymentStatus());
            return bookingRepository.save(bookingToUpdate);
        }
        throw new RuntimeException("Booking not found");
    }

    @Transactional
    public String uploadPaymentSlip(Integer bookingId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);

        // Save file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Update booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setPaymentSlipPath(filename);
        booking.setPaymentStatus("Submitted");
        bookingRepository.save(booking);

        return filename;
    }

    @Transactional
    public void updatePaymentStatus(Integer bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setPaymentStatus(status);

        if ("Approved".equalsIgnoreCase(status)) {
            booking.setBookingStatus("Confirmed");
            // Update vehicle status
            Vehicle vehicle = booking.getVehicle();
            vehicle.setAvailabilityStatus("Booked");
            vehicleRepository.save(vehicle);
        }

        bookingRepository.save(booking);
    }

    @Transactional
    public void updateBookingStatus(Integer bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String oldStatus = booking.getBookingStatus();
        booking.setBookingStatus(status);

        // Update vehicle availability when booking is completed or cancelled
        if ("Completed".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status)) {
            Vehicle vehicle = booking.getVehicle();
            vehicle.setAvailabilityStatus("Available");
            vehicleRepository.save(vehicle);
        }

        bookingRepository.save(booking);
    }

    public Optional<Booking> findById(Integer id) {
        return bookingRepository.findById(id);
    }

    public Optional<Booking> findByIdWithDetails(Integer id) {
        return bookingRepository.findByIdWithDetails(id);
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public List<Booking> findByCustomerId(Integer customerId) {
        return bookingRepository.findByCustomer_CustomerId(customerId);
    }

    public List<Booking> findByStatus(String status) {
        return bookingRepository.findByBookingStatus(status);
    }

    public List<Booking> findPendingPayments() {
        return bookingRepository.findPendingPaymentBookings();
    }

    public List<Booking> findRecentBookings() {
        return bookingRepository.findRecentBookings();
    }

    @Transactional
    public void deleteBooking(Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Release vehicle if booking is cancelled
        Vehicle vehicle = booking.getVehicle();
        vehicle.setAvailabilityStatus("Available");
        vehicleRepository.save(vehicle);

        bookingRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return bookingRepository.countByBookingStatus(status);
    }

    public long countByPaymentStatus(String status) {
        return bookingRepository.countByPaymentStatus(status);
    }

    public long getTotalBookings() {
        return bookingRepository.count();
    }
}

