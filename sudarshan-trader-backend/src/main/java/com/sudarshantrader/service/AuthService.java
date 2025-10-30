package com.sudarshantrader.service;

import com.sudarshantrader.dto.*;
import com.sudarshantrader.entity.User;
import com.sudarshantrader.repository.UserRepository;
import com.sudarshantrader.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public String register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already exists");
        if (userRepository.existsByGst(req.getGst()))
            throw new RuntimeException("GST already exists");

        User user = new User();
        user.setCompanyName(req.getCompanyName());
        user.setContactPerson(req.getContactPerson());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setGst(req.getGst());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole(User.Role.BUYER);
        user.setVerified(false);

        userRepository.save(user);
        return "Registration successful. Awaiting admin verification.";
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Invalid credentials");

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(token, user.getRole().name(), user.isVerified());
    }

    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponse(
                user.getId(), user.getCompanyName(), user.getContactPerson(), user.getEmail(),
                user.getPhone(), user.getGst(), user.getRole().name(), user.isVerified());
    }
}
