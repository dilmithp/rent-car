package com.carrentalsystem.service;

import com.carrentalsystem.models.Customer;
import com.carrentalsystem.models.User;
import com.carrentalsystem.repository.CustomerRepository;
import com.carrentalsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Customer createCustomer(Customer customer, String username, String password) {
        // Check if email or license already exists
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (customerRepository.existsByLicenseNumber(customer.getLicenseNumber())) {
            throw new RuntimeException("License number already exists");
        }

        // Create user account
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserRole("CUSTOMER");
        user.setEnabled(true);
        user = userRepository.save(user);

        // Link customer to user
        customer.setUser(user);
        customer.setStatus("Active");

        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Customer customer) {
        Optional<Customer> existingCustomer = customerRepository.findById(customer.getCustomerId());
        if (existingCustomer.isPresent()) {
            Customer customerToUpdate = existingCustomer.get();
            customerToUpdate.setFullName(customer.getFullName());
            customerToUpdate.setEmail(customer.getEmail());
            customerToUpdate.setPhone(customer.getPhone());
            customerToUpdate.setAddress(customer.getAddress());
            customerToUpdate.setLicenseNumber(customer.getLicenseNumber());
            customerToUpdate.setStatus(customer.getStatus());
            return customerRepository.save(customerToUpdate);
        }
        throw new RuntimeException("Customer not found");
    }

    public Optional<Customer> findById(Integer id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> findByUserId(Integer userId) {
        return customerRepository.findByUser_UserId(userId);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public List<Customer> findAllActive() {
        return customerRepository.findAllActiveCustomers();
    }

    public List<Customer> searchCustomers(String keyword) {
        return customerRepository.searchCustomers(keyword);
    }

    @Transactional
    public void deleteCustomer(Integer id) {
        customerRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return customerRepository.countByStatus(status);
    }
}

