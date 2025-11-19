package com.carrentalsystem.repository;

import com.carrentalsystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Find by username
    Optional<User> findByUsername(String username);

    // Find by role
    List<User> findByUserRole(String role);

    // Check if username exists
    boolean existsByUsername(String username);

    // Find all enabled users
    List<User> findByEnabled(boolean enabled);

    // Count users by role
    long countByUserRole(String role);

    // Find all customers (users with CUSTOMER role)
    @Query("SELECT u FROM User u WHERE u.userRole = 'CUSTOMER' ORDER BY u.createdAt DESC")
    List<User> findAllCustomers();
}

