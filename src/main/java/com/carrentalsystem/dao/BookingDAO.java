package com.carrentalsystem.dao;

import com.carrentalsystem.database.DatabaseConnection;
import com.carrentalsystem.models.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private Connection connection;

    public BookingDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean addBooking(Booking booking) {
        String query = "INSERT INTO bookings (customer_id, vehicle_id, booking_date, start_date, end_date, total_days, total_amount, booking_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, booking.getCustomerId());
            pstmt.setInt(2, booking.getVehicleId());
            pstmt.setDate(3, booking.getBookingDate());
            pstmt.setDate(4, booking.getStartDate());
            pstmt.setDate(5, booking.getEndDate());
            pstmt.setInt(6, booking.getTotalDays());
            pstmt.setBigDecimal(7, booking.getTotalAmount());
            pstmt.setString(8, booking.getBookingStatus());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Booking getBookingById(int bookingId) {
        String query = "SELECT * FROM bookings WHERE booking_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractBookingFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting booking by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all bookings: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getBookingsByCustomer(int customerId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE customer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bookings by customer: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getBookingsByVehicle(int vehicleId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE vehicle_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bookings by vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getPendingBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE booking_status = 'pending'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending bookings: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean updateBooking(Booking booking) {
        String query = "UPDATE bookings SET customer_id = ?, vehicle_id = ?, booking_date = ?, start_date = ?, end_date = ?, total_days = ?, total_amount = ?, booking_status = ? WHERE booking_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, booking.getCustomerId());
            pstmt.setInt(2, booking.getVehicleId());
            pstmt.setDate(3, booking.getBookingDate());
            pstmt.setDate(4, booking.getStartDate());
            pstmt.setDate(5, booking.getEndDate());
            pstmt.setInt(6, booking.getTotalDays());
            pstmt.setBigDecimal(7, booking.getTotalAmount());
            pstmt.setString(8, booking.getBookingStatus());
            pstmt.setInt(9, booking.getBookingId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        String query = "UPDATE bookings SET booking_status = ? WHERE booking_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, bookingId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePaymentStatus(int bookingId, String paymentStatus, String paymentSlipPath) {
        String query = "UPDATE bookings SET payment_status = ?, payment_slip_path = ? WHERE booking_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, paymentStatus);
            pstmt.setString(2, paymentSlipPath);
            pstmt.setInt(3, bookingId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBooking(int bookingId) {
        String query = "DELETE FROM bookings WHERE booking_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, bookingId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setCustomerId(rs.getInt("customer_id"));
        booking.setVehicleId(rs.getInt("vehicle_id"));
        booking.setBookingDate(rs.getDate("booking_date"));
        booking.setStartDate(rs.getDate("start_date"));
        booking.setEndDate(rs.getDate("end_date"));
        booking.setTotalDays(rs.getInt("total_days"));
        booking.setTotalAmount(rs.getBigDecimal("total_amount"));
        booking.setBookingStatus(rs.getString("booking_status"));
        booking.setPaymentStatus(rs.getString("payment_status"));
        booking.setPaymentSlipPath(rs.getString("payment_slip_path"));
        booking.setCreatedAt(rs.getTimestamp("created_at"));
        return booking;
    }
}
