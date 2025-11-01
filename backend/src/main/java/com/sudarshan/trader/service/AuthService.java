package com.sudarshan.trader.service;

import com.sudarshan.trader.config.JwtUtil;
import com.sudarshan.trader.dto.AuthResponse;
import com.sudarshan.trader.dto.LoginRequest;
import com.sudarshan.trader.dto.RegisterRequest;
import com.sudarshan.trader.dto.UserDTO;
import com.sudarshan.trader.entity.User;
import com.sudarshan.trader.exception.DuplicateResourceException;
import com.sudarshan.trader.exception.ResourceNotFoundException;
import com.sudarshan.trader.exception.UnauthorizedException;
import com.sudarshan.trader.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        // Validate email not already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        // Validate GST number unique
        if (userRepository.existsByGstNumber(request.getGstNumber())) {
            throw new DuplicateResourceException("GST number already in use");
        }

        // Hash password using BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create new User entity with role=BUYER, verified=false
        User user = new User();
        user.setCompanyName(request.getCompanyName());
        user.setContactPerson(request.getContactPerson());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGstNumber(request.getGstNumber());
        user.setPasswordHash(hashedPassword);
        user.setRole("BUYER");
        user.setVerified(false);

        // Save to database
        user = userRepository.save(user);

        // Return success message (do NOT auto-login, user pending verification)
        return new AuthResponse("Registration successful. Your account is pending verification.", user.getId());
    }

    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        // Verify password using BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // Generate JWT token with expiry (24 hours) using JwtUtil
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // Return token + user info
        UserDTO userDTO = mapToUserDTO(user);
        return new AuthResponse(token, userDTO);
    }

    public UserDTO getCurrentUser(String email) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Return user DTO
        return mapToUserDTO(user);
    }

    private UserDTO mapToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setCompanyName(user.getCompanyName());
        dto.setContactPerson(user.getContactPerson());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setGstNumber(user.getGstNumber());
        dto.setRole(user.getRole());
        dto.setVerified(user.getVerified());
        return dto;
    }
}
