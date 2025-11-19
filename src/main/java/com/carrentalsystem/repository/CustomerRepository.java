package com.carrentalsystem.repository;

import com.carrentalsystem.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // Find by user ID
    Optional<Customer> findByUser_UserId(Integer userId);

    // Find by email
    Optional<Customer> findByEmail(String email);

    // Find by license number
    Optional<Customer> findByLicenseNumber(String licenseNumber);

    // Find by status
    List<Customer> findByStatus(String status);

    // Search customers by name, email or phone
    @Query("SELECT c FROM Customer c WHERE LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR c.phone LIKE CONCAT('%', :keyword, '%')")
    List<Customer> searchCustomers(@Param("keyword") String keyword);

    // Count customers by status
    long countByStatus(String status);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if license number exists
    boolean existsByLicenseNumber(String licenseNumber);

    // Find all active customers
    @Query("SELECT c FROM Customer c WHERE c.status = 'Active' ORDER BY c.fullName")
    List<Customer> findAllActiveCustomers();
}

