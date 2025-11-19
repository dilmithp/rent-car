package com.carrentalsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"bookings"})
@ToString(exclude = {"bookings"})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @NotBlank(message = "Vehicle number is required")
    @Column(name = "vehicle_number", unique = true, nullable = false, length = 50)
    private String vehicleNumber;

    @NotBlank(message = "Brand is required")
    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    @NotBlank(message = "Model is required")
    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @NotBlank(message = "Vehicle type is required")
    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;

    @Min(value = 1900, message = "Year must be valid")
    @Max(value = 2100, message = "Year must be valid")
    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "color", length = 30)
    private String color;

    @NotNull(message = "Daily rate is required")
    @DecimalMin(value = "0.01", message = "Daily rate must be greater than 0")
    @Column(name = "daily_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "availability_status", length = 20, nullable = false)
    private String availabilityStatus = "Available";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (availabilityStatus == null) {
            availabilityStatus = "Available";
        }
    }
}
