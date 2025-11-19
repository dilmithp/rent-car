package com.carrentalsystem.repository;

import com.carrentalsystem.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    // Find by availability status
    List<Vehicle> findByAvailabilityStatus(String status);

    // Find by vehicle type
    List<Vehicle> findByVehicleType(String type);

    // Find by vehicle number
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    // Find available vehicles by type
    List<Vehicle> findByAvailabilityStatusAndVehicleType(String status, String type);

    // Search vehicles by brand or model
    @Query("SELECT v FROM Vehicle v WHERE LOWER(v.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(v.model) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Vehicle> searchVehicles(@Param("keyword") String keyword);

    // Count vehicles by status
    long countByAvailabilityStatus(String status);

    // Find all available vehicles
    @Query("SELECT v FROM Vehicle v WHERE v.availabilityStatus = 'Available' ORDER BY v.brand, v.model")
    List<Vehicle> findAllAvailableVehicles();
}

