package com.carrentalsystem.dao;

import com.carrentalsystem.database.DatabaseConnection;
import com.carrentalsystem.models.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    private Connection connection;

    public VehicleDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean addVehicle(Vehicle vehicle) {
        String query = "INSERT INTO vehicles (vehicle_number, brand, model, vehicle_type, year, color, daily_rate, availability_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, vehicle.getVehicleNumber());
            pstmt.setString(2, vehicle.getBrand());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setString(4, vehicle.getVehicleType());
            pstmt.setInt(5, vehicle.getYear());
            pstmt.setString(6, vehicle.getColor());
            pstmt.setBigDecimal(7, vehicle.getDailyRate());
            pstmt.setString(8, vehicle.getAvailabilityStatus());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding vehicle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Vehicle getVehicleById(int vehicleId) {
        String query = "SELECT * FROM vehicles WHERE vehicle_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractVehicleFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicle by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Vehicle getVehicleByNumber(String vehicleNumber) {
        String query = "SELECT * FROM vehicles WHERE vehicle_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, vehicleNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractVehicleFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicle by number: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM vehicles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        return vehicles;
    }

    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM vehicles WHERE availability_status = 'available'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting available vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        return vehicles;
    }

    public List<Vehicle> getVehiclesByType(String vehicleType) {
        List<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM vehicles WHERE vehicle_type = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, vehicleType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicles by type: " + e.getMessage());
            e.printStackTrace();
        }
        return vehicles;
    }

    public boolean updateVehicle(Vehicle vehicle) {
        String query = "UPDATE vehicles SET vehicle_number = ?, brand = ?, model = ?, vehicle_type = ?, year = ?, color = ?, daily_rate = ?, availability_status = ? WHERE vehicle_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, vehicle.getVehicleNumber());
            pstmt.setString(2, vehicle.getBrand());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setString(4, vehicle.getVehicleType());
            pstmt.setInt(5, vehicle.getYear());
            pstmt.setString(6, vehicle.getColor());
            pstmt.setBigDecimal(7, vehicle.getDailyRate());
            pstmt.setString(8, vehicle.getAvailabilityStatus());
            pstmt.setInt(9, vehicle.getVehicleId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateVehicleStatus(int vehicleId, String status) {
        String query = "UPDATE vehicles SET availability_status = ? WHERE vehicle_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, vehicleId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating vehicle status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVehicle(int vehicleId) {
        String query = "DELETE FROM vehicles WHERE vehicle_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, vehicleId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Vehicle extractVehicleFromResultSet(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(rs.getInt("vehicle_id"));
        vehicle.setVehicleNumber(rs.getString("vehicle_number"));
        vehicle.setBrand(rs.getString("brand"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setVehicleType(rs.getString("vehicle_type"));
        vehicle.setYear(rs.getInt("year"));
        vehicle.setColor(rs.getString("color"));
        vehicle.setDailyRate(rs.getBigDecimal("daily_rate"));
        vehicle.setAvailabilityStatus(rs.getString("availability_status"));
        vehicle.setCreatedAt(rs.getTimestamp("created_at"));
        return vehicle;
    }
}
