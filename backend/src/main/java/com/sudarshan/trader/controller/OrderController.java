package com.sudarshan.trader.controller;

import com.sudarshan.trader.dto.OrderRequest;
import com.sudarshan.trader.dto.OrderResponse;
import com.sudarshan.trader.dto.PaymentVerificationRequest;
import com.sudarshan.trader.dto.UserDTO;
import com.sudarshan.trader.service.AuthService;
import com.sudarshan.trader.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthService authService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        UserDTO user = authService.getCurrentUser(email);

        OrderResponse response = orderService.createOrder(user.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(Authentication authentication) {
        String email = authentication.getName();
        UserDTO user = authService.getCurrentUser(email);

        // Check if user is admin
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        List<OrderResponse> orders;
        if (isAdmin) {
            orders = orderService.getAllOrders();
        } else {
            orders = orderService.getBuyerOrders(user.getId());
        }

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        UserDTO user = authService.getCurrentUser(email);

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_BUYER")
                .replace("ROLE_", "");

        OrderResponse order = orderService.getOrderById(id, user.getId(), role);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/payment/verify")
    public ResponseEntity<Map<String, String>> verifyPayment(
            @Valid @RequestBody PaymentVerificationRequest request) {

        orderService.handlePaymentSuccess(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        Map<String, String> response = new HashMap<>();
        response.put("message", "Payment verified successfully");
        response.put("orderStatus", "CONFIRMED");
        response.put("paymentStatus", "PAID");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment/failure")
    public ResponseEntity<Map<String, String>> handlePaymentFailure(
            @RequestParam String razorpayOrderId) {

        orderService.handlePaymentFailure(razorpayOrderId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Payment failed");
        response.put("orderStatus", "CANCELLED");

        return ResponseEntity.ok(response);
    }
}
