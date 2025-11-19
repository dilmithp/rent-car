package com.carrentalsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"customer", "vehicle", "payment"})
@ToString(exclude = {"customer", "vehicle", "payment"})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer is required")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "Vehicle is required")
    private Vehicle vehicle;

    @Column(name = "booking_date", nullable = false)
    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Column(name = "total_days", nullable = false)
    private Integer totalDays;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    @Column(name = "booking_status", length = 20, nullable = false)
    private String bookingStatus = "Pending";

    @Column(name = "payment_status", length = 20, nullable = false)
    private String paymentStatus = "Pending";

    @Column(name = "payment_slip_path", length = 500)
    private String paymentSlipPath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (bookingStatus == null) {
            bookingStatus = "Pending";
        }
        if (paymentStatus == null) {
            paymentStatus = "Pending";
        }
        if (bookingDate == null) {
            bookingDate = LocalDate.now();
        }
    }

    // Helper methods for backward compatibility
    @Transient
    public Integer getCustomerId() {
        return customer != null ? customer.getCustomerId() : null;
    }

    @Transient
    public Integer getVehicleId() {
        return vehicle != null ? vehicle.getVehicleId() : null;
    }
}
