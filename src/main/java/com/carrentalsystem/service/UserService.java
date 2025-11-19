package com.carrentalsystem.service;

import com.carrentalsystem.models.User;
import com.carrentalsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getUserId());
        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            userToUpdate.setUsername(user.getUsername());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userToUpdate.setUserRole(user.getUserRole());
            userToUpdate.setEnabled(user.isEnabled());
            return userRepository.save(userToUpdate);
        }
        throw new RuntimeException("User not found");
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(String role) {
        return userRepository.findByUserRole(role);
    }

    @Transactional
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public long countByRole(String role) {
        return userRepository.countByUserRole(role);
    }
}

