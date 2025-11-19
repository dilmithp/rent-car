package com.carrentalsystem.service;

import com.carrentalsystem.models.Vehicle;
import com.carrentalsystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Transactional
    public Vehicle createVehicle(Vehicle vehicle) {
        if (vehicleRepository.findByVehicleNumber(vehicle.getVehicleNumber()).isPresent()) {
            throw new RuntimeException("Vehicle number already exists");
        }
        vehicle.setAvailabilityStatus("Available");
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle updateVehicle(Vehicle vehicle) {
        Optional<Vehicle> existingVehicle = vehicleRepository.findById(vehicle.getVehicleId());
        if (existingVehicle.isPresent()) {
            Vehicle vehicleToUpdate = existingVehicle.get();
            vehicleToUpdate.setVehicleNumber(vehicle.getVehicleNumber());
            vehicleToUpdate.setBrand(vehicle.getBrand());
            vehicleToUpdate.setModel(vehicle.getModel());
            vehicleToUpdate.setVehicleType(vehicle.getVehicleType());
            vehicleToUpdate.setYear(vehicle.getYear());
            vehicleToUpdate.setColor(vehicle.getColor());
            vehicleToUpdate.setDailyRate(vehicle.getDailyRate());
            vehicleToUpdate.setAvailabilityStatus(vehicle.getAvailabilityStatus());
            return vehicleRepository.save(vehicleToUpdate);
        }
        throw new RuntimeException("Vehicle not found");
    }

    public Optional<Vehicle> findById(Integer id) {
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> findAllAvailable() {
        return vehicleRepository.findAllAvailableVehicles();
    }

    public List<Vehicle> findByStatus(String status) {
        return vehicleRepository.findByAvailabilityStatus(status);
    }

    public List<Vehicle> findByType(String type) {
        return vehicleRepository.findByVehicleType(type);
    }

    public List<Vehicle> searchVehicles(String keyword) {
        return vehicleRepository.searchVehicles(keyword);
    }

    @Transactional
    public void deleteVehicle(Integer id) {
        vehicleRepository.deleteById(id);
    }

    @Transactional
    public void updateAvailabilityStatus(Integer vehicleId, String status) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        vehicle.setAvailabilityStatus(status);
        vehicleRepository.save(vehicle);
    }

    public long countByStatus(String status) {
        return vehicleRepository.countByAvailabilityStatus(status);
    }

    public long getTotalVehicles() {
        return vehicleRepository.count();
    }
}

